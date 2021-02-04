package me.phantomclone.levelpvp.map;

import me.phantomclone.api.region.Region;
import me.phantomclone.api.region.RegionHandler;
import me.phantomclone.eventmanager.EventListener;
import me.phantomclone.eventmanager.EventManager;
import me.phantomclone.levelpvp.LevelPvP;
import me.phantomclone.levelpvp.gui.KitChoiceGui;
import me.phantomclone.levelpvp.util.ClickableItem;
import me.phantomclone.levelpvp.util.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class MapHandler {

    private final LevelPvP plugin;
    private String folder = "./plugins/levelpvp/regions/";

    private RegionHandler regionHandler;

    private Region region;

    private EventListener<PlayerJoinEvent> join;
    private EventListener<PlayerDeathEvent> death;
    private EventListener<PlayerRespawnEvent> respawn;
    private EventListener<EntityDamageByEntityEvent> damage;
    private EventListener<PlayerDropItemEvent> drop;
    private EventListener<PlayerPickupItemEvent> pickup;

    private ClickableItem kitselector;
    private ClickableItem menu;

    public MapHandler(LevelPvP plugin) {
        this.plugin = plugin;
    }

    public void init() {
        this.regionHandler = new RegionHandler(this.folder);

        join = event -> {
            if (region != null && region.getSpawn() != null) {
                event.getPlayer().teleport(region.getSpawn());
                setInAreaInv(event.getPlayer());
            }
        };

        death = event -> event.getEntity().spigot().respawn();

        respawn = event -> {
            if (this.region != null) {
                event.getPlayer().setVelocity(new Vector(0,0,0));
                event.setRespawnLocation(this.region.getSpawn());
            }
        };

        damage = event -> {
            if (event.getEntity() instanceof Player) {
                if (region.playersInRegion.contains(event.getEntity())) {
                    event.setCancelled(true);
                } else if (event.getDamager() instanceof Player && region.playersInRegion.contains(event.getDamager())) {
                    event.setCancelled(true);
                }
            }
        };

        drop = event -> event.setCancelled(true);

        pickup = event -> event.setCancelled(true);

        this.kitselector = new ClickableItem(new ItemBuilder(Material.CHEST).setDisplayname("§aKit Wahl").build(), (player, block) -> new KitChoiceGui(plugin, player, 0).openInventory(player), plugin.getEventManager());
        this.menu = new ClickableItem(new ItemBuilder(Material.NETHER_STAR).setDisplayname("§6Menu").build(), (player, block) -> {
            if (player.hasPermission("levelpvp.admin")) {
                plugin.getGuiHandler().openMenu(player);
            }
        }, plugin.getEventManager());
    }

    public void setInAreaInv(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);
        this.kitselector.giveItemToPlayer(player, 4);
        if (player.hasPermission("levelpvp.admin")) {
            this.menu.giveItemToPlayer(player, 8);
        }
    }

    public void start(EventManager eventManager) {
        this.regionHandler.loadRegions();

        List<Region> list = this.regionHandler.getRegions();
        if (!list.isEmpty())
        list.stream().filter(Region::isLoaded).forEach(region -> {
            region.setLoaded(false);
            region.save(this.folder);
        });
        Region[] regionList = list.stream().filter(Region::isEnabled).toArray(Region[]::new);
        if (region != null || regionList.length != 0) {
            this.region = regionList[new Random().nextInt(regionList.length)];
            this.region.setLoaded(true);
            this.regionHandler.start(this.plugin);
        }

        eventManager.registerEvent(PlayerJoinEvent.class, join);
        eventManager.registerEvent(PlayerDeathEvent.class, death);
        eventManager.registerEvent(PlayerRespawnEvent.class, respawn);
        eventManager.registerEvent(EntityDamageByEntityEvent.class, damage);
        eventManager.registerEvent(PlayerDropItemEvent.class, drop);
        eventManager.registerEvent(PlayerPickupItemEvent.class, pickup);

        if (region != null)
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.teleport(region.getSpawn());
            setInAreaInv(player);
        });
    }

    public void stop(EventManager eventManager) {
        kitselector.destroy();
        menu.destroy();
        eventManager.unregisterEvent(PlayerJoinEvent.class, join);
        eventManager.unregisterEvent(PlayerDeathEvent.class, death);
        eventManager.unregisterEvent(PlayerRespawnEvent.class, respawn);
        eventManager.unregisterEvent(EntityDamageByEntityEvent.class, damage);
        eventManager.unregisterEvent(PlayerDropItemEvent.class, drop);
        eventManager.unregisterEvent(PlayerPickupItemEvent.class, pickup);
        this.regionHandler.stop();
    }

    public RegionHandler getRegionHandler() {
        return regionHandler;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}

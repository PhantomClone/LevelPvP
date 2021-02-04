package me.phantomclone.levelpvp;

import me.phantomclone.api.region.RegionMoveEvent;
import me.phantomclone.api.region.RegionState;
import me.phantomclone.eventmanager.EventListener;
import me.phantomclone.eventmanager.EventManager;
import me.phantomclone.levelpvp.gui.GuiHandler;
import me.phantomclone.levelpvp.gui.KitChoiceGui;
import me.phantomclone.levelpvp.kit.*;
import me.phantomclone.levelpvp.level.LevelHandler;
import me.phantomclone.levelpvp.map.MapHandler;
import me.phantomclone.levelpvp.map.MapChangeHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class LevelPvP extends JavaPlugin {

    private static LevelPvP instance;

    private MapHandler mapHandler;
    private KitHandler kitHandler;
    private LevelHandler levelHandler;
    private GuiHandler guiHandler;
    private MapChangeHandler mapChangeHandler;

    private EventManager eventManager;

    private EventListener<AsyncPlayerChatEvent> chatEvent;
    private EventListener<RegionMoveEvent> regionMoveEvent;

    private ArrayList<Location> points = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        this.eventManager = new EventManager(this);
        this.mapHandler = new MapHandler(this);
        this.kitHandler = new KitHandler(this);
        this.levelHandler = new LevelHandler(this);
        this.mapChangeHandler = new MapChangeHandler(this);

        this.levelHandler.connectToMySQL();

        this.mapHandler.init();

        this.mapHandler.start(this.eventManager);
        if (this.mapHandler.getRegionHandler().getRegions().size() == 0) {
            getServer().getConsoleSender().sendMessage("Es wurden noch keine Maps erstellt!");
        }

        initEvents();

        this.kitHandler.loadKits();
        this.levelHandler.loadPlayers();

        this.levelHandler.startTimer();

        this.guiHandler = new GuiHandler(this);

        this.mapChangeHandler.initConfig("./plugins/levelpvp/");
        this.mapChangeHandler.startTimer();
    }

    @Override
    public void onDisable() {
        this.eventManager.unregisterEvent(RegionMoveEvent.class, regionMoveEvent);
        this.eventManager.unregisterEvent(AsyncPlayerChatEvent.class, chatEvent);
        this.mapHandler.stop(this.eventManager);
        this.levelHandler.disconnect();
        this.mapHandler = null;
        instance = null;
    }

    private void initEvents() {
        /*
        ChatEvent is only there for testing and will changed to the Command '/LevelPvP'
         */
        this.chatEvent = event -> {
            if (!event.getPlayer().hasPermission("LevelPvP.Admin")) return;
            if (event.getMessage().contains("add")) {
                double x = event.getPlayer().getLocation().getX();
                double y = event.getPlayer().getLocation().getY();
                double z = event.getPlayer().getLocation().getZ();
                this.points.add(new Location(event.getPlayer().getLocation().getWorld(), Math.round(x), y, Math.round(z)));
                event.getPlayer().sendMessage("added!");

                event.setCancelled(true);
            } else if (event.getMessage().contains("ddf")) {
                event.getPlayer().getWorld().getEntities().stream().filter(entity -> entity instanceof Item && !((Item) entity).getItemStack().getType().equals(Material.LEASH)).forEach(Entity::remove);
                event.setCancelled(true);
            } else if (event.getMessage().contains("done")) {
                int y = (int) event.getPlayer().getLocation().getY();
                this.mapHandler.getRegionHandler().createRegion(event.getMessage().replace("done",""), points, y, y + 3, event.getPlayer().getLocation(), true, false);
                event.getPlayer().sendMessage("Erstellt!");
                event.setCancelled(true);
            } else if (event.getMessage().contains("create")) {
                Kit kit = new Kit();
                kit.setName(event.getMessage().replace("create", ""));
                kit.setPrefix("§c" + kit.getName());
                kit.setLevel(1);
                kit.setEnabled(true);
                kit.setItem(event.getPlayer().getInventory().getItemInHand());
                kit.setInventory(event.getPlayer().getInventory().getContents());
                kit.setArmor(event.getPlayer().getInventory().getArmorContents());
                Effect effect = new Effect();
                effect.setDuration(10);
                effect.setPower(1);
                effect.setType(EffectEnum.SPEED);
                kit.getEventEffects().get(EventEnum.EQUIP).add(new Effect(effect.toString()));
                kit.save();
                event.setCancelled(true);
            } else if (event.getMessage().contains("equ")) {
                Kit kit = new Kit();
                kit.load(event.getMessage().replace("equ", ""));
                kit.equip(event.getPlayer());
                event.setCancelled(true);
            } else if (event.getMessage().contains("menu")) {
                this.getGuiHandler().openMenu(event.getPlayer());
                event.setCancelled(true);
            } else if (event.getMessage().contains("open")) {
                int page = Integer.parseInt(event.getMessage().replace("open", ""));
                KitChoiceGui gui = new KitChoiceGui(this, event.getPlayer(), page);
                gui.openInventory(event.getPlayer());
                event.setCancelled(true);
            }
        };
        this.regionMoveEvent = event -> {
            if (event.getState().equals(RegionState.LEAVES)) {
                this.kitHandler.equipKit(event.getPlayer());
            } else if (event.getState().equals(RegionState.ENTER)) {
                this.mapHandler.setInAreaInv(event.getPlayer());
            }
        };
        this.eventManager.registerEvent(RegionMoveEvent.class, this.regionMoveEvent);
        this.eventManager.registerEvent(AsyncPlayerChatEvent.class, this.chatEvent);
    }

    public MapHandler getMapHandler() {
        return mapHandler;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public KitHandler getKitHandler() {
        return kitHandler;
    }

    public GuiHandler getGuiHandler() {
        return guiHandler;
    }

    public MapChangeHandler getMapChangeHandler() {
        return mapChangeHandler;
    }

    public static LevelPvP getInstance() {
        return instance;
    }

}

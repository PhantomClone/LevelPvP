package me.phantomclone.levelpvp.kit;

import me.phantomclone.eventmanager.EventListener;
import me.phantomclone.levelpvp.LevelPvP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Kit {

    private String name;
    private String prefix;
    private boolean enabled;
    private int level;
    private ItemStack item;
    private ItemStack[] inventory;
    private ItemStack[] armor;

    private HashMap<EventEnum, List<Effect>> eventEffects;

    public List<Player> players;

    private EventListener<EntityDamageByEntityEvent> damage;

    public Kit() {
        this.eventEffects = new HashMap<>();
        Arrays.stream(EventEnum.values()).forEach(e -> this.eventEffects.put(e, new ArrayList<>()));
        this.players = new ArrayList<>();
        damage = event -> {
            if (event.getDamager() != null && event.getDamager() instanceof Player && !event.getDamager().equals(event.getEntity()) && players.contains(event.getDamager())) {
                if (event.getEntity() instanceof Player && LevelPvP.getInstance().getMapHandler().getRegion().playersInRegion.contains(event.getEntity())) {
                    return;
                }
                if (LevelPvP.getInstance().getMapHandler().getRegion().playersInRegion.contains(event.getDamager())) {
                    return;
                }
                if (!(event.getEntity() instanceof Player)) {
                    return;
                }
                if (((Player) event.getEntity()).getHealth() - event.getFinalDamage() <= 0) {
                    eventEffects.get(EventEnum.KILL).forEach(effect -> effect.handle((Player) event.getDamager(), EventEnum.KILL));
                }
                setMetadata((Player) event.getEntity(), "lastDmg", event.getDamager().getName());
                eventEffects.get(EventEnum.HIT).forEach(effect -> effect.handle((Player) event.getDamager(), EventEnum.HIT));
            }
        };
    }

    public void unregister() {
        LevelPvP.getInstance().getEventManager().unregisterEvent(EntityDamageByEntityEvent.class, damage);
    }

    public void equip(Player player) {
        Bukkit.getScheduler().runTask(LevelPvP.getInstance(), () -> {
            player.getInventory().setContents(this.inventory);
            player.getInventory().setArmorContents(this.armor);
            eventEffects.get(EventEnum.EQUIP).forEach(effect -> effect.handle(player, EventEnum.EQUIP));
            player.setHealth(player.getMaxHealth());
        });
    }

    public Kit load(String name) {
        File file = new File(LevelPvP.getInstance().getKitHandler().getFolder() + name + ".yml");
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            this.name = name;
            this.prefix = config.getString("prefix").replace("&", "§");
            this.enabled = config.getBoolean("enabled");
            this.level = config.getInt("level");
            this.item = config.getItemStack("item");
            this.inventory = config.getList("inventory").toArray(new ItemStack[0]);
            this.armor = config.getList("armor").toArray(new ItemStack[0]);
            this.eventEffects.keySet().forEach(e -> {
                List<Effect> list = new ArrayList<>();
                config.getStringList(e.name()).forEach(s -> list.add(new Effect(s)));
                eventEffects.put(e, list);
            });
            //LevelPvP.getInstance().getEventManager().registerEvent(PlayerDeathEvent.class, death);
            LevelPvP.getInstance().getEventManager().registerEvent(EntityDamageByEntityEvent.class, damage);
            return this;
        }
        return null;
    }

    public void save() {
        File dir = new File(LevelPvP.getInstance().getKitHandler().getFolder());
        dir.mkdirs();
        File file = new File(dir, name + ".yml");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("prefix", this.prefix.replace("§", "&"));
        config.set("enabled", this.enabled);
        config.set("level", this.level);
        config.set("item", setName(this.item));
        List<ItemStack> inv = new ArrayList<>();
        for (ItemStack itemStack : this.inventory) {
            inv.add(setName(itemStack));
        }
        config.set("inventory", inv);
        List<ItemStack> armor = new ArrayList<>();
        for (ItemStack itemStack : this.armor) {
            armor.add(setName(itemStack));
        }
        config.set("armor", armor);
        eventEffects.entrySet().forEach(set -> {
            ArrayList<String> list = new ArrayList<>();
            set.getValue().forEach(s -> list.add(s.toString()));
            config.set(set.getKey().name(), list);
        });
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ItemStack setName(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (Arrays.stream(ItemMeta.class.getMethods()).anyMatch(m -> m.getName().equalsIgnoreCase("spigot"))) {
            meta.spigot().setUnbreakable(true);
        } else {
            Method method = Arrays.stream(ItemMeta.class.getMethods()).filter(m -> m.getName().equalsIgnoreCase("setUnbreakable")).findFirst().orElse(null);
            try {
                if (method != null)
                    method.invoke(meta, true);
            } catch (IllegalAccessException | InvocationTargetException e) {
            }
        }
        meta.setDisplayName(this.prefix);
        item.setItemMeta(meta);
        return item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public HashMap<EventEnum, List<Effect>> getEventEffects() {
        return eventEffects;
    }


    private void removeMetadata(Player player, String name) {
        if(player.hasMetadata(name))
            player.removeMetadata(name, LevelPvP.getInstance());
    }

    private void setMetadata(Player player, String name, Object value) {
        removeMetadata(player, name);
        player.setMetadata(name, new FixedMetadataValue(LevelPvP.getInstance(), value));
    }
}

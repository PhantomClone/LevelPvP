package me.phantomclone.levelpvp.kit;

import me.phantomclone.levelpvp.LevelPvP;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class KitHandler {

    private final LevelPvP plugin;
    private String folder = "./plugins/levelpvp/kits/";

    private List<Kit> loadedKits;

    public KitHandler(LevelPvP plugin) {
        this.plugin = plugin;
        this.loadedKits = new ArrayList<>();
    }

    public void addPlayerToKit(Player player, Kit kit) {
        loadedKits.forEach(k -> k.players.remove(player));
        kit.players.add(player);
        player.sendMessage("§7Du hast dass Kit " + kit.getPrefix() + " §7ausgerüstet!");
    }

    public boolean deleteKit(Kit kit) {
        File file = new File(folder + kit.getName() + ".yml");
        if (file.delete()) {
            loadedKits.remove(kit);
            kit.unregister();
            return true;
        }
        return false;
    }

    public void equipKit(Player player) {
        Kit kit = loadedKits.stream().filter(k -> k.players.contains(player)).findFirst().orElse(null);
        if (kit == null) {
            if (!loadedKits.isEmpty()) {
                kit = defaultKit(0);
                if (kit == null) {
                    return;
                }
                kit.players.add(player);
                kit.equip(player);
            }
            return;
        }
        kit.equip(player);
    }

    private Kit defaultKit(int i) {
        if (i >= loadedKits.size())
            return null;
        Kit kit = loadedKits.get(i);
        return kit.isEnabled() ? kit : defaultKit(i + 1);
    }

    public void loadKit(String name) {
        if (getKit(name) == null) {
            Kit kit = new Kit().load(name);
            if (kit != null) {
                loadedKits.add(kit);
            }
        }
    }

    public void loadKits() {
        File dir = new File(folder);
        dir.mkdirs();
        Arrays.stream(dir.listFiles()).filter(file -> file.getName().endsWith(".yml")).forEach(file -> {
            Kit kit = new Kit().load(file.getName().replace(".yml", ""));
            if (kit != null) {
                loadedKits.add(kit);
            }
        });
        loadedKits.sort(Comparator.comparingInt(Kit::getLevel));
    }

    public Kit getKit(String kitname) {
        return loadedKits.stream().filter(kit -> kit.getName().equalsIgnoreCase(kitname)).findFirst().orElse(null);
    }

    public List<Kit> getLoadedKits() {
        return loadedKits;
    }

    public String getFolder() {
        return folder;
    }
}

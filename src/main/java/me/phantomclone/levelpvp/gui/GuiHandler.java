package me.phantomclone.levelpvp.gui;

import me.phantomclone.levelpvp.LevelPvP;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuiHandler {

    private final LevelPvP plugin;

    private MenuGui menuGui;
    private MapChangeGui mapChangeGui;
    private List<MapGui> guiMaps;
    private List<KitEditGui> kitedit;

    public GuiHandler(LevelPvP plugin) {
        this.plugin = plugin;

        this.menuGui = new MenuGui(plugin);
        this.mapChangeGui = new MapChangeGui(plugin);
        this.guiMaps = new ArrayList<>();
        this.kitedit = new ArrayList<>();
    }

    public void openMenu(Player player) {
        menuGui.openInventory(player);
    }

    public void openMapChange(Player player) {
        this.mapChangeGui.openInventory(player);
    }

    public void openMapGui(Player player, int page) {
        MapGui map = guiMaps.stream().filter(gui -> gui.getPage() == page).findFirst().orElse(newGuiMap(page));
        plugin.getServer().getScheduler().runTask(plugin, () -> map.openInventory(player));
    }

    public void openKitEdit(Player player, int page) {
        KitEditGui kit = kitedit.stream().filter(gui -> gui.getPage() == page).findFirst().orElse(newGuiKitEdit(page));
        kit.setKitItems();
        plugin.getServer().getScheduler().runTask(plugin, () -> kit.openInventory(player));
    }

    public KitEditGui getKitEdit(int page) {
        KitEditGui kit = kitedit.stream().filter(gui -> gui.getPage() == page).findFirst().orElse(newGuiKitEdit(page));
        kit.setKitItems();
        return kit;
    }

    private MapGui newGuiMap(int page) {
        MapGui map = new MapGui(plugin, page);
        guiMaps.add(map);
        return map;
    }

    private KitEditGui newGuiKitEdit(int page) {
        KitEditGui kit = new KitEditGui(plugin, page);
        kitedit.add(kit);
        return kit;
    }

    public void updateMap() {
        this.guiMaps.forEach(map -> map.updateMapItems());
    }

    public MapChangeGui getMapChangeGui() {
        return mapChangeGui;
    }
}

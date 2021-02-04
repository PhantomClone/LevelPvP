package me.phantomclone.levelpvp.gui;

import me.phantomclone.api.inventorygui.ClickItem;
import me.phantomclone.api.inventorygui.InventoryGui;
import me.phantomclone.api.region.Region;
import me.phantomclone.anvilgui.api.Anvilgui;
import me.phantomclone.levelpvp.LevelPvP;
import me.phantomclone.levelpvp.map.CreateMap;
import me.phantomclone.levelpvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MapGui extends InventoryGui {

    private LevelPvP plugin;

    private int page;

    public MapGui(LevelPvP plugin, int page) {
        super(plugin, plugin.getServer().createInventory(null, 9 * 5, "§8Menu §7- §8Map§7(§d" + (page + 1) + "§7)"));
        this.plugin = plugin;
        this.page = page;
        initListener();
        setFillItem(new ItemBuilder(Material.BARRIER).setDisplayname("§7").build());
        ItemStack black = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setDisplayname("§7").build();
        for (int i = 0; i < 9; i++) {
            setItem(i, black);
            setItem(i + 4 * 9, black);
        }
        for (int i = 0; i < 3; i++) {
            setItem(9 + 9 * i, black);
            setItem(17 + 9 * i, black);
        }

        if (page != 0) {
            setItem(0, new ClickItem(new ItemStack(Material.DOUBLE_PLANT)).setDisplayname("§7<- §8Page §7(§d" + page + "§7)").setClicked(player -> new MapGui(plugin, page - 1).openInventory(player)));
        }
        setItem(4, new ClickItem(new ItemStack(Material.ANVIL)).setDisplayname("Create Region").setClicked(player -> {
            new Anvilgui.Builder().plugin(plugin).item(new ItemStack(Material.EMPTY_MAP)).text("Map Name...").onComplete((p, s) -> {
                if (plugin.getMapHandler().getRegionHandler().getRegion(s) != null) {
                    return Anvilgui.Response.text("Name existiert schon");
                } else {
                    new CreateMap(plugin, p, s);
                    return Anvilgui.Response.close();
                }
            }).open(player);
        }));

        setItem(8, new ClickItem(new ItemStack(Material.DOUBLE_PLANT)).setDisplayname("§8Page §7(§d" + (page + 2) + "§7) ->").setClicked(player -> new MapGui(plugin, page + 1).openInventory(player)));

        setItem(18, new ClickItem(new ItemStack(Material.STONE_BUTTON)).setDisplayname("§cZurück zum Menu").setClicked(player -> this.plugin.getGuiHandler().openMenu(player)));

        updateMapItems();
    }

    public void updateMapItems() {
        List<Region> regions = plugin.getMapHandler().getRegionHandler().getRegions();
        int count = 3 * 7 * page;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {
                if (count >= regions.size()) break;
                Region region = regions.get(count);
                ItemBuilder item = new ItemBuilder(region.isLoaded() ? Material.MAP : Material.EMPTY_MAP).setDisplayname((region.isLoaded() ? "§a" : "§c") + region.getName());
                if (!region.isEnabled()) item.setLore("Deaktiviert!");
                setItem(convertSlot(j, i), new ClickItem(item.build()).setClicked(player -> new MapSettingsGui(plugin, region).openInventory(player)));
                count++;
            }
        }
        update();
    }

    private int convertSlot(int x, int y) {
        return y * 9 + 9 + x + 1;
    }

    public int getPage() {
        return page;
    }

    private class MapSettingsGui extends InventoryGui {

        private LevelPvP plugin;

        public MapSettingsGui(LevelPvP plugin, Region region) {
            super(plugin, plugin.getServer().createInventory(null, 9, "§8Menu &7- §8Map §7- §8" + region.getName()));
            this.plugin = plugin;
            setDestroyOnClose(true);
            setFillItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setDisplayname("§7").build());
            initListener();
            updateMap(region);
        }

        private void updateMap(Region region) {
            setItem(0, new ItemBuilder(Material.PAPER).setDisplayname("§7Infos").setLore("§7Name: §5" + region.getName(), "§7Status: " + (region.isEnabled() ? region.isLoaded() ? "§2Running" : "§aEnabled" : "§cDisabled")).build());
            setItem(2, new ClickItem(new ItemBuilder(Material.STAINED_CLAY, 1 , region.isEnabled() ? (short) 13 : (short) 14).setDisplayname("§7Klicke um die Map zu " + (region.isEnabled() ? "§cdeaktivieren" : "§aaktivieren")).build()).setClicked(player -> {
                region.setEnabled(!region.isEnabled());
                region.save(plugin.getMapHandler().getRegionHandler().getFolder());
                player.sendMessage("§7Map §5" + region.getName() + " §7wurde " + (region.isEnabled() ? "§aaktiviert" : "§cdeaktiviert") + "§7!");
                updateMap(region);
                MapGui.this.updateMapItems();
            }));
            boolean forced = region.equals(plugin.getMapChangeHandler().getForce());
            setItem(4, new ClickItem(new ItemBuilder(Material.STAINED_GLASS, 1, forced ? (short) 13 : (short) 14).setDisplayname("§7Forced").setLore("§8Klicke zum verändern").build()).setClicked(player -> {
                if (region.equals(plugin.getMapChangeHandler().getForce())) {
                    plugin.getMapChangeHandler().setForce(null);
                } else {
                    plugin.getMapChangeHandler().setForce(region);
                }
                updateMap(region);
            }));
            setItem(6, new ClickItem(new ItemStack(Material.BARRIER)).setDisplayname("§4Löschen").setClicked(player -> {
                if (region.isLoaded()) {
                    player.sendMessage("§7Die Map wird derzeit benutzt!");
                    return;
                }
                if (plugin.getMapHandler().getRegionHandler().deleteRegion(region)) {
                    player.sendMessage("§7Die Map §5" + region.getName() + " §7wurde gelöscht!");
                    MapGui.this.updateMapItems();
                    MapGui.this.openInventory(player);
                } else {
                    player.sendMessage("§7Die Map " + region.getName() + " konnte nicht gelöscht werden!");
                }
            }));
            setItem(8, new ClickItem(new ItemStack(Material.DOUBLE_PLANT)).setDisplayname("§7Zurück...").setClicked(player -> MapGui.this.openInventory(player)));
            update();
        }
    }
}

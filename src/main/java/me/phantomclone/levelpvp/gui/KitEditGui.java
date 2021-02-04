package me.phantomclone.levelpvp.gui;

import me.phantomclone.anvilgui.api.Anvilgui;
import me.phantomclone.api.inventorygui.ClickItem;
import me.phantomclone.api.inventorygui.InventoryGui;
import me.phantomclone.levelpvp.LevelPvP;
import me.phantomclone.levelpvp.kit.CreateKit;
import me.phantomclone.levelpvp.kit.Kit;
import me.phantomclone.levelpvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitEditGui extends InventoryGui {

    private LevelPvP plugin;
    private int page;

    public KitEditGui(LevelPvP plugin, int page) {
        super(plugin, plugin.getServer().createInventory(null, 9*6, "§8Menu §7- §8Kit Edit"));
        this.plugin = plugin;
        this.page = page;
        setFillItem(new ItemBuilder(Material.BARRIER).setDisplayname("§7").build());

        ItemStack black = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setDisplayname("§7").build();

        for (int i = 0; i < 9; i++) {
            setItem(i, black);
            setItem(i + 5 * 9, black);
        }
        for (int i = 9; i < 45; i += 9) {
            setItem(i, black);
            setItem(i + 8, black);
        }
        if (page != 0) {
            setItem(0, new ClickItem(new ItemStack(Material.DOUBLE_PLANT)).setDisplayname("§7<- §8Page §7(§d" + page + "§7)").setClicked(player -> plugin.getGuiHandler().openKitEdit(player, page - 1)));
        }
        setItem(4, new ClickItem(new ItemStack(Material.ANVIL)).setDisplayname("§7Create Kit").setClicked(player -> new CreateKit(plugin, player)));
        setItem(8, new ClickItem(new ItemStack(Material.DOUBLE_PLANT)).setDisplayname("§8Page §7(§d" + (page + 2) + "§7) ->").setClicked(player -> plugin.getGuiHandler().openKitEdit(player, page + 1)));

        setItem(18, new ClickItem(new ItemStack(Material.STONE_BUTTON)).setDisplayname("§cZurück zum Menu").setClicked(player -> this.plugin.getGuiHandler().openMenu(player)));

        initListener();
        setKitItems();
    }

    public void setKitItems() {
        int size = plugin.getKitHandler().getLoadedKits().size();
        int count = 4 * 7 * page;
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 7; i++) {
                if (size <= count) {
                    setItem(convertSlot(i, j), getFillItem());
                } else {
                    setItem(convertSlot(i, j), createClickKititem(plugin.getKitHandler().getLoadedKits().get(count)));
                }
                count++;
            }
        }
        update();
    }


    private ClickItem createClickKititem(Kit kit) {
        ItemBuilder builder = new ItemBuilder(kit.getItem()).setDisplayname(kit.getPrefix());
        builder.setLore("Kit name - " + kit.getName(), "", "Benötigte Level - §5" + kit.getLevel(), "", kit.isEnabled() ? "Kit ist §aAktiviert!" : "Kit ist §cDeaktiviert!");

        return new ClickItem(builder.build(), player -> new KitEditorGui(plugin, kit).openInventory(player));
    }

    public void openEditorGui(Player player, Kit kit) {
        new KitEditorGui(plugin, kit).openInventory(player);
    }

    public int getPage() {
        return page;
    }

    private int convertSlot(int x, int y) {
        return y * 9 + 9 + x + 1;
    }

    private class KitEditorGui extends InventoryGui {

        public KitEditorGui(LevelPvP plugin, Kit kit) {
            super(plugin, plugin.getServer().createInventory(null, 9, "§8Menu §7- §8Kit Edit §7- §8" + kit.getName()));
            setDestroyOnClose(true);
            setFillItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setDisplayname("§7").build());

            setItem(8, new ClickItem(new ItemStack(Material.DOUBLE_PLANT)).setDisplayname("§7Zurück...").setClicked(KitEditGui.this::openInventory));
            initListener();
            updateKit(kit);
        }

        private void updateKit(Kit kit) {
            setItem(0, new ItemBuilder(Material.BOOK).setDisplayname(kit.getPrefix()).setLore("§7Name: §d" + kit.getName(), "§7Prefix: §r" + kit.getPrefix(), "§7Level: §d" + kit.getLevel()).build());
            setItem(1, new ClickItem(Material.NAME_TAG).setDisplayname("§7Displayname: " + kit.getPrefix()).setClicked(player -> {
                new Anvilgui.Builder().plugin(plugin).text("Prefix...").item(new ItemStack(Material.NAME_TAG)).onComplete((p, s) -> {
                    if (s == null || s.isEmpty()) {
                        p.sendMessage("§7Text darf nicht leer sein!");
                        return Anvilgui.Response.text("Prefix...");
                    }
                    kit.setPrefix(s.replace("&", "§"));
                    p.sendMessage("§7Prefix wurde geändert!");
                    kit.save();
                    p.sendMessage("§7Prefix änderung wurde gespeichert!");
                    return Anvilgui.Response.close();
                }).open(player);
            }));
            setItem(2, new ClickItem(Material.EXP_BOTTLE).setDisplayname("§7Level: " + kit.getLevel()).setClicked(player -> {
                new Anvilgui.Builder().plugin(plugin).text("Level...").item(new ItemStack(Material.EXP_BOTTLE)).onComplete((p, s) -> {
                    int i = -1;
                    try {
                        i = Integer.parseInt(s);
                    } catch (Exception e) {
                        player.sendMessage("§7Nur Zahlen sind erlaubt!");
                        return Anvilgui.Response.text("Level...");
                    }
                    kit.setLevel(i);
                    p.sendMessage("§7Level wurde geändert!");
                    kit.save();
                    p.sendMessage("§7Level änderung wurde gespeichert!");
                    return Anvilgui.Response.close();
                }).open(player);
            }));
            setItem(3, new ClickItem(Material.COMMAND).setDisplayname("§7Edit Effects").setClicked(player -> {
                new EffectChangeGui(plugin, kit, 0).openInventory(player);
            }));
            setItem(4, new ClickItem(new ItemBuilder(Material.STAINED_CLAY, 1 , kit.isEnabled() ? (short) 13 : (short) 14).setDisplayname("§7Klicke um das Kit zu " + (kit.isEnabled() ? "§cdeaktivieren" : "§aaktivieren")).build()).setClicked(player -> {
                kit.setEnabled(!kit.isEnabled());
                kit.save();
                player.sendMessage("§7Kit §5" + kit.getName() + " §7wurde " + (kit.isEnabled() ? "§aaktiviert" : "§cdeaktiviert") + "§7!");
                updateKit(kit);
                setKitItems();
            }));
            setItem(6, new ClickItem(new ItemStack(Material.BARRIER)).setDisplayname("§4Löschen").setClicked(player -> {
                if (kit.isEnabled()) {
                    player.sendMessage("§7Das Kit ist noch Aktiviert!");
                    return;
                }
                if (!kit.players.isEmpty()) {
                    player.sendMessage("§7Das Kit wird noch benutzt!");
                    return;
                }
                if (plugin.getKitHandler().deleteKit(kit)) {
                    player.sendMessage("§7Das Kit §5" + kit.getName() + " §7wurde gelöscht!");
                    KitEditGui.this.setKitItems();
                    KitEditGui.this.openInventory(player);
                } else {
                    player.sendMessage("§7Das Kit " + kit.getName() + " konnte nicht gelöscht werden!");
                }
            }));
            update();
        }
    }

}

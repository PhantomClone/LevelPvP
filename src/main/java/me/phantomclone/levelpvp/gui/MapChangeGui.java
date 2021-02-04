package me.phantomclone.levelpvp.gui;

import me.phantomclone.anvilgui.api.Anvilgui;
import me.phantomclone.api.inventorygui.ClickItem;
import me.phantomclone.api.inventorygui.InventoryGui;
import me.phantomclone.api.region.Region;
import me.phantomclone.levelpvp.LevelPvP;
import me.phantomclone.levelpvp.map.MapChangeHandler;
import me.phantomclone.levelpvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MapChangeGui extends InventoryGui {

    private LevelPvP plugin;

    public MapChangeGui(LevelPvP plugin) {
        super(plugin, plugin.getServer().createInventory(null, 9, "§8Menu §7- §8Map Timer"));
        this.plugin = plugin;
        initListener();

        setFillItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setDisplayname("§7").build());

        setItem(8, new ClickItem(new ItemStack(Material.DOUBLE_PLANT)).setDisplayname("§7Zurück...").setClicked(player -> plugin.getGuiHandler().openMenu(player)));
        updateItems(plugin.getMapChangeHandler());
    }

    public void updateItems(MapChangeHandler changer) {
        int time = changer.getSectochange() - changer.getTime();
        boolean pause = changer.isPause();
        Region force = changer.getForce();
        setItem(0, new ClickItem(new ItemBuilder(Material.WATCH).setDisplayname("§7Set Map Timer").setLore("§dTime: §c" + time + "§ds").build()).setClicked(player ->  new Anvilgui.Builder().plugin(plugin).text("Zeit in Sekunden...").onClose(this::openInventory).onComplete((p, s) -> {
                try {
                    int i = Integer.parseInt(s);
                    if (i < 5) {
                        player.sendMessage("§7Mindestens 5 Sekunden...");
                        return Anvilgui.Response.text("Zeit in Sekunden...");
                    }
                    changer.setTime(changer.getSectochange() - i);
                    player.sendMessage("§7Map Timer wurde auf §d" + i + " §7Sekunden gestellt!");
                    return Anvilgui.Response.close();
                } catch (Exception e) {
                    player.sendMessage("§7Nur Zahlen sind erlaubt!");
                    return Anvilgui.Response.text("Zeit in Sekunden...");
                }
            }).open(player)));

        setItem(2, new ClickItem(new ItemBuilder(Material.STAINED_CLAY, 1 , !pause ? (short) 13 : (short) 14).setDisplayname("§7Klicke um den Map Timer zu " + (!pause ? "§cdeaktivieren" : "§aaktivieren")).build()).setClicked(player -> {
            changer.setPause(!changer.isPause());
            player.sendMessage("§7Map Timer ist nun " + (changer.isPause() ? "§cdeaktivieren" : "§aaktivieren") + "§7.");
            updateItems(changer);
        }));

        setItem(4, force == null ? getFillItem() : new ItemBuilder(Material.MAP).setDisplayname("§7Next Map: §d" + force.getName()).build());
        update();
    }
}

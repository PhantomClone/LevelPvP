package me.phantomclone.levelpvp.gui;

import me.phantomclone.api.inventorygui.ClickItem;
import me.phantomclone.api.inventorygui.InventoryGui;
import me.phantomclone.levelpvp.LevelPvP;
import me.phantomclone.levelpvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MenuGui extends InventoryGui {

    public MenuGui(LevelPvP plugin) {
        super(plugin, plugin.getServer().createInventory(null, 9 * 3, "§8Menu"));
        setFillItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setDisplayname("§7").build());

        setItem(10, new ClickItem(new ItemStack(Material.DIAMOND_AXE)).setDisplayname("§3Kit Manager").setClicked(player -> plugin.getGuiHandler().openKitEdit(player, 0)));
        setItem(13, new ClickItem(new ItemStack(Material.MAP)).setDisplayname("§6Map Manager").setClicked(player -> plugin.getGuiHandler().openMapGui(player, 0)));
        setItem(16, new ClickItem(new ItemStack(Material.WATCH)).setDisplayname("§7Map Timer").setClicked(player -> plugin.getGuiHandler().openMapChange(player)));

        initListener();

        update();
    }

    public void updateTimer(int time) {
        setItem(16, new ItemBuilder(Material.WATCH).setDisplayname("§7Set Map Timer").setLore("Time: §c" + time + "§7s").build());
        update();
    }
}

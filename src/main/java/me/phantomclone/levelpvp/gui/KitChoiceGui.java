package me.phantomclone.levelpvp.gui;

import me.phantomclone.api.inventorygui.ClickItem;
import me.phantomclone.api.inventorygui.InventoryGui;
import me.phantomclone.levelpvp.LevelPvP;
import me.phantomclone.levelpvp.kit.Kit;
import me.phantomclone.levelpvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitChoiceGui extends InventoryGui {

    private LevelPvP plugin;

    private int page;
    private int playerlevel;

    public KitChoiceGui(LevelPvP plugin, Player player, int page) {
        super(plugin, plugin.getServer().createInventory(player, 9 * 6, "Kits - " + (page + 1)));
        this.plugin = plugin;
        this.page = page;
        setDestroyOnClose(true);
        if (player.hasMetadata("level"))
            this.playerlevel = player.getMetadata("level").get(0).asInt();
        else
            this.playerlevel = 0;

        setFillItem(new ItemBuilder(Material.BARRIER).setDisplayname("§7").build());

        ItemStack black = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setDisplayname("§7").build();

        for (int i = 0; i < 9; i++) {
            setItem(i, black.clone());
            setItem(i + 5 * 9, black.clone());
        }
        for (int i = 9; i < 45; i += 9) {
            setItem(i, black.clone());
            setItem(i + 8, black.clone());
        }

        if (page != 0) {
            setItem(0, new ClickItem(new ItemStack(Material.DOUBLE_PLANT)).setDisplayname("§7<- §8Page §7(§d" + page + "§7)").setClicked(p -> new KitChoiceGui(plugin, p, page - 1).openInventory(player)));
        }
        setItem(8, new ClickItem(new ItemStack(Material.DOUBLE_PLANT)).setDisplayname("§8Page §7(§d" + (page + 2) + "§7) ->").setClicked(p -> new KitChoiceGui(plugin, p, page + 1).openInventory(player)));

        initListener();
        setKits();
        update();
    }

    private void setKits() {
        int size = plugin.getKitHandler().getLoadedKits().size();
        int count = 4 * 7 * page;
        if (count >= size)
            return;
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 7; i++) {
                setItem(convertSlot(i , j), createClickKititem(plugin.getKitHandler().getLoadedKits().get(count)));
                count++;
                if (size == count)
                    return;
            }
        }
    }

    private ClickItem createClickKititem(Kit kit) {
        ItemBuilder builder = new ItemBuilder(kit.getItem()).setDisplayname(kit.getPrefix());
        if (kit.isEnabled()) {
            builder.setLore("Kit name - " + kit.getName(), "", "Benötigte Level - " + (playerlevel >= kit.getLevel() ? "§a" : "§c") + kit.getLevel());
        } else {
            builder.setLore("Kit name - " + kit.getName(), "", "Benötigte Level - " + (playerlevel >= kit.getLevel() ? "§a" : "§c") + kit.getLevel(), "", "Kit ist §cDeaktiviert!");
        }

        return new ClickItem(builder.build(), player -> {
            if (!kit.isEnabled()) {
                player.sendMessage("§7Kit ist der Zeit Deaktiviert!");
                return;
            }
            if (playerlevel >= kit.getLevel()) {
                plugin.getKitHandler().addPlayerToKit(player, kit);
            } else {
                player.sendMessage("§7Du benötigst für dieses Kit mindestens das Level §c" + kit.getLevel() + "§7!");
            }
        });
    }

    private int convertSlot(int x, int y) {
        return y * 9 + 9 + x + 1;
    }
}

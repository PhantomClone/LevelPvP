package me.phantomclone.levelpvp.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder() {}

    public ItemBuilder(ItemStack itemstack) {
        this.item = itemstack;
    }

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }
    public ItemBuilder(Material material, int amount, short damage) {
        this.item = new ItemStack(material, amount, damage);
    }

    public ItemBuilder setDisplayname(String displayname) {
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayname);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setAmount(int amount ) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemStack build() {
        return this.item;
    }

}

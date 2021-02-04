package me.phantomclone.levelpvp.kit;

import org.bukkit.Material;

public enum EventEnum {

    EQUIP(Material.DIAMOND_CHESTPLATE), HIT(Material.REDSTONE), KILL(Material.DIAMOND_SWORD);

    Material material;

    EventEnum(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }
}

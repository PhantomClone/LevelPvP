package me.phantomclone.levelpvp.kit;

import me.phantomclone.levelpvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public enum EffectEnum {

    MAX_HEAL(PotionEffectType.HEALTH_BOOST, new ItemBuilder(Material.GOLDEN_APPLE).setDisplayname("§cMax Heath").build()),
    REGENERATION(PotionEffectType.REGENERATION, new ItemBuilder(Material.POTION).setDisplayname("§dRegeneration").build()),
    SPEED(PotionEffectType.SPEED, new ItemBuilder(Material.POTION).setDisplayname("§fSpeed").build()),
    FIRE_RESISTANCE(PotionEffectType.FIRE_RESISTANCE, new ItemBuilder(Material.POTION).setDisplayname("§6Fire Resistance").build()),
    POISON(PotionEffectType.POISON, new ItemBuilder(Material.POTION).setDisplayname("§2Poison").build()),
    HEALING(PotionEffectType.HEAL, new ItemBuilder(Material.POTION).setDisplayname("§cHeal").build()), //HEAL
    HEALTH_BOOST(PotionEffectType.HEALTH_BOOST, new ItemBuilder(Material.POTION).setDisplayname("§6Heal Boost").build()),
    NIGHT_VISION(PotionEffectType.NIGHT_VISION, new ItemBuilder(Material.POTION).setDisplayname("§8Night Vision").build()),
    WEAKNESS(PotionEffectType.WEAKNESS, new ItemBuilder(Material.POTION).setDisplayname("§8Weakness").build()),
    INCREASE_DAMAGE(PotionEffectType.INCREASE_DAMAGE, new ItemBuilder(Material.POTION).setDisplayname("§4Strength").build()),
    SLOW(PotionEffectType.SLOW, new ItemBuilder(Material.POTION).setDisplayname("§8Slow").build()),
    SLOW_DIGGING(PotionEffectType.SLOW_DIGGING, new ItemBuilder(Material.WOOD_PICKAXE).setDisplayname("§bSlow Digging").build()),
    FAST_DIGGING(PotionEffectType.FAST_DIGGING, new ItemBuilder(Material.DIAMOND_PICKAXE).setDisplayname("§3Fast Digging").build()),
    JUMP(PotionEffectType.JUMP, new ItemBuilder(Material.POTION).setDisplayname("§2Jump Boost").build()),
    DAMAGE_RESISTANCE(PotionEffectType.DAMAGE_RESISTANCE, new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplayname("§8Damage Resistance").build()),
    WATER_BREATHING(PotionEffectType.WATER_BREATHING, new ItemBuilder(Material.POTION).setDisplayname("§1Water Breathing").build()),
    INVISIBILITY(PotionEffectType.INVISIBILITY, new ItemBuilder(Material.POTION).setDisplayname("§fInvisibility").build()),
    HUNGER(PotionEffectType.HUNGER, new ItemBuilder(Material.ROTTEN_FLESH).setDisplayname("§aHunger").build()),
    HARM(PotionEffectType.HARM, new ItemBuilder(Material.POTION).setDisplayname("§0Schaden").build()), //DMG
    CONFUSION(PotionEffectType.CONFUSION, new ItemBuilder(Material.SOUL_SAND).setDisplayname("§dConfusion").build()),
    WITHER(PotionEffectType.WITHER, new ItemBuilder(Material.SKULL_ITEM, 1, (short) 1).setDisplayname("§0Wither").build());

    PotionEffectType effect;
    ItemStack item;

    EffectEnum(PotionEffectType effect, ItemStack item) {
        this.effect = effect;
        this.item = item;
    }

    public PotionEffectType getEffect() {
        return effect;
    }

    public ItemStack getItem() {
        return item;
    }

    /*
    MAX_HEAL(PotionEffectType.HEALTH_BOOST, new ItemBuilder(Material.GOLDEN_APPLE, 1, (short) 1).setDisplayname("§cMax Heath").build()),
    REGENERATION(PotionEffectType.REGENERATION, new ItemBuilder(Material.POTION, 1, (short) 8193).setDisplayname("§dRegeneration").build()),
    SPEED(PotionEffectType.SPEED, new ItemBuilder(Material.POTION, 1, (short) 8194).setDisplayname("§fSpeed").build()),
    FIRE_RESISTANCE(PotionEffectType.FIRE_RESISTANCE, new ItemBuilder(Material.POTION, 1, (short) 8227).setDisplayname("§6Fire Resistance").build()),
    POISON(PotionEffectType.POISON, new ItemBuilder(Material.POTION, 1, (short) 8196).setDisplayname("§2Poison").build()),
    HEALING(PotionEffectType.HEAL, new ItemBuilder(Material.POTION, 1, (short) 8261).setDisplayname("§cHeal").build()), //HEAL
    HEALTH_BOOST(PotionEffectType.HEALTH_BOOST, new ItemBuilder(Material.POTION, 1, (short) 8261).setDisplayname("§6Heal Boost").build()),
    NIGHT_VISION(PotionEffectType.NIGHT_VISION, new ItemBuilder(Material.POTION, 1, (short) 8230).setDisplayname("§8Night Vision").build()),
    WEAKNESS(PotionEffectType.WEAKNESS, new ItemBuilder(Material.POTION, 1, (short) 8232).setDisplayname("§8Weakness").build()),
    INCREASE_DAMAGE(PotionEffectType.INCREASE_DAMAGE, new ItemBuilder(Material.POTION, 1, (short) 8201).setDisplayname("§4Strength").build()),
    SLOW(PotionEffectType.SLOW, new ItemBuilder(Material.POTION, 1, (short) 8234).setDisplayname("§8Slow").build()),
    SLOW_DIGGING(PotionEffectType.SLOW_DIGGING, new ItemBuilder(Material.WOOD_PICKAXE).setDisplayname("§bSlow Digging").build()),
    FAST_DIGGING(PotionEffectType.FAST_DIGGING, new ItemBuilder(Material.DIAMOND_PICKAXE).setDisplayname("§3Fast Digging").build()),
    JUMP(PotionEffectType.JUMP, new ItemBuilder(Material.POTION, 1, (short) 8203).setDisplayname("§2Jump Boost").build()),
    DAMAGE_RESISTANCE(PotionEffectType.DAMAGE_RESISTANCE, new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplayname("§8Damage Resistance").build()),
    WATER_BREATHING(PotionEffectType.WATER_BREATHING, new ItemBuilder(Material.POTION).setDisplayname("§1Water Breathing").build()),
    INVISIBILITY(PotionEffectType.INVISIBILITY, new ItemBuilder(Material.POTION, 1, (short) 8238).setDisplayname("§fInvisibility").build()),
    HUNGER(PotionEffectType.HUNGER, new ItemBuilder(Material.ROTTEN_FLESH).setDisplayname("§aHunger").build()),
    HARM(PotionEffectType.HARM, new ItemBuilder(Material.POTION, 1, (short) 8268).setDisplayname("§0Schaden").build()), //DMG
    CONFUSION(PotionEffectType.CONFUSION, new ItemBuilder(Material.SOUL_SAND).setDisplayname("§dConfusion").build()),
    WITHER(PotionEffectType.WITHER, new ItemBuilder(Material.SKULL_ITEM, 1, (short) 1).setDisplayname("§0Wither").build());
     */
}

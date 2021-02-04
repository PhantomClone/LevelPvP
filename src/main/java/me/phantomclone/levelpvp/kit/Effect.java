package me.phantomclone.levelpvp.kit;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class Effect {

    private EffectEnum type;

    private int power;
    private int duration;

    public Effect() {}

    public Effect(String fromString) {
        String[] args = fromString.replace("Effect{", "").replace("}", "").split(", ");
        this.type = EffectEnum.valueOf(args[0].replace("type=", ""));
        this.power = Integer.parseInt(args[1].replace("power=", ""));
        this.duration = Integer.parseInt(args[2].replace("duration=", ""));
    }

    public void handle(Player player, EventEnum event) {
        switch (type) {
            case MAX_HEAL:
                player.setMaxHealth(event.equals(EventEnum.EQUIP) ? power : (player.getMaxHealth() + power));
                break;
            case HEALING:
                int heal = (int) player.getHealth() + power;
                player.setHealth(heal > player.getMaxHealth() ? player.getMaxHealth() : heal);
                break;
            case HARM:
                int live = (int) player.getHealth() - power;
                player.setHealth(live < 0 ? 0 : live);
                break;
            default:
                if (player.hasPotionEffect(type.effect))
                    player.removePotionEffect(type.effect);
                player.addPotionEffect(new PotionEffect(type.effect, duration == -1 ? Integer.MAX_VALUE : duration * 20, power - 1, false, false));
                break;
        }
    }

    @Override
    public String toString() {
        return "Effect{" + "type=" + type.name().toUpperCase() + ", power=" + power + ", duration=" + duration + "}";
    }

    public EffectEnum getType() {
        return type;
    }

    public void setType(EffectEnum type) {
        this.type = type;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

package me.phantomclone.levelpvp.kit;

import me.phantomclone.anvilgui.api.Anvilgui;
import me.phantomclone.eventmanager.EventListener;
import me.phantomclone.levelpvp.LevelPvP;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CreateKit {

    private EventListener<PlayerInteractEvent> interactevent;

    private String name;
    private String prefix;
    private int level = -1;

    public CreateKit(LevelPvP plugin, Player player) {
        this.interactevent = event -> {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                if (event.getPlayer().getInventory().getItemInHand() == null) {
                    event.getPlayer().sendMessage("§7Halte ein Item als Symbol...");
                    return;
                }
                new Anvilgui.Builder().plugin(plugin).text("Kitname...").onClose(p -> {
                    destroy(plugin);
                    plugin.getMapHandler().setInAreaInv(player);
                }).onComplete((p, s) -> {
                    if (this.name == null) {
                        if (plugin.getKitHandler().getKit(s) != null) {
                            p.sendMessage("§7Kit name bereits vergeben!");
                            return Anvilgui.Response.text("Kitname...");
                        }
                        if (s == null || s.isEmpty()) {
                            p.sendMessage("§7Kitname darf nicht leer sein!");
                            return Anvilgui.Response.text("Kitname...");
                        }
                        this.name = s;
                        return Anvilgui.Response.text("Kitprefix...");
                    }
                    if (this.prefix == null) {
                        if (s == null || s.isEmpty()) {
                            p.sendMessage("§7Kitprefix darf nicht leer sein!");
                            return Anvilgui.Response.text("Kitprefix...");
                        }
                        this.prefix = s.replace("&", "§");
                        return Anvilgui.Response.text("Level...");
                    }
                    if (level == -1) {
                        try {
                            this.level = Integer.parseInt(s);
                            if (player.getInventory().getItemInHand() == null) {
                                player.sendMessage("§7Du musst ein Item in der Hand halten...");
                                destroy(plugin);
                                return Anvilgui.Response.close();
                            }
                            Kit kit = new Kit();
                            kit.setName(this.name);
                            kit.setPrefix(this.prefix);
                            kit.setEnabled(false);
                            kit.setInventory(player.getInventory().getContents());
                            kit.setArmor(player.getInventory().getArmorContents());
                            kit.setLevel(this.level);
                            kit.setItem(player.getInventory().getItemInHand());
                            player.sendMessage("§7Kit wurde erstellt!");
                            kit.save();
                            player.sendMessage("§7Kit wurde gespeichert!");
                            plugin.getKitHandler().loadKit(name);
                            player.sendMessage("§7Kit wurde geladen!");
                        } catch (Exception e) {
                            player.sendMessage("§7Nur zahlen sind erlaubt als Level");
                            return Anvilgui.Response.text("Level...");
                        }
                    }
                    return Anvilgui.Response.close();
                }).open(player);
            }
        };
        plugin.getEventManager().registerEvent(PlayerInteractEvent.class, interactevent);
        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().clear();
        player.closeInventory();
        player.sendMessage("§7Erstelle dein Kit...");
        player.sendMessage("§7Wenn du fertig bist, nimm das Show Item in die Hand und Rechtsllicke.");
    }

    private void destroy(LevelPvP plugin) {
        plugin.getEventManager().unregisterEvent(PlayerInteractEvent.class, interactevent);
    }

}

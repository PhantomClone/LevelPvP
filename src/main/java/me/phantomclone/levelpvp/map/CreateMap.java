package me.phantomclone.levelpvp.map;

import me.phantomclone.api.region.Region;
import me.phantomclone.eventmanager.EventListener;
import me.phantomclone.levelpvp.LevelPvP;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class CreateMap {

    private List<Location> points;

    private EventListener<PlayerInteractEvent> interactevent;

    public CreateMap(LevelPvP plugin, Player player, String name) {
        this.points = new ArrayList<>();

        this.interactevent = event -> {
            if (event.getPlayer().equals(player) && !event.getAction().equals(Action.PHYSICAL)) {
                event.setCancelled(true);
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                    double x = event.getPlayer().getLocation().getX();
                    double y = event.getPlayer().getLocation().getY();
                    double z = event.getPlayer().getLocation().getZ();
                    points.add(new Location(event.getPlayer().getLocation().getWorld(), Math.round(x), y, Math.round(z)));
                    player.sendMessage("§8Point added!");
                } else {
                    if (points.size() < 3) {
                        player.sendMessage("Du musst mindestens 3 Punkte markieren!");
                        return;
                    }
                    if (plugin.getMapHandler().getRegionHandler().getRegion(name) != null) {
                        player.sendMessage("§7Eine Map mit dem name §d" + name + " §7wurde bereits erstellt!");
                        destroy(plugin);
                        return;
                    }
                    final int[] hights = new int[] {(int) Math.round(points.get(0).getY()), (int) Math.round(points.get(0).getY())};
                    points.forEach(location -> {
                        int high = (int) Math.round(location.getY());
                        if (high < hights[0]) {
                            hights[0] = high;
                        } else if (high > hights[1]) {
                            hights[1] = high;
                        }
                    });
                    if (hights[1] - hights[0] < 2) {
                        player.sendMessage("§7Der Höhen unterschied muss mindestens §c2 §7Blöcke sein!");
                        destroy(plugin);
                        return;
                    }
                    MapHandler mapHandler = plugin.getMapHandler();
                    mapHandler.getRegionHandler().createRegion(name, points, hights[0], hights[1], player.getLocation(), true, false);
                    if (mapHandler.getRegionHandler().getRegions().size() == 1) {
                        mapHandler.setRegion(plugin.getMapHandler().getRegionHandler().getRegion(name));
                        plugin.getServer().getOnlinePlayers().forEach(p -> {
                            p.teleport(player.getLocation());
                            mapHandler.setInAreaInv(p);
                        });
                    }
                    player.sendMessage("§7Map §d" + name + " §7wurde erfolgreich erstellt, geladen und aktiviert!");
                    plugin.getGuiHandler().updateMap();
                    destroy(plugin);
                }
            }
        };
        plugin.getEventManager().registerEvent(PlayerInteractEvent.class, interactevent);

        player.sendMessage("§7Rechtsclicker um Eckpunkte zu Markieren (mind. 3)");
        player.sendMessage("§7Achte hierbei auf einen Höhen unterschied von dem höchsten und dem niedrigsten Punkt von mind. 2 Blöcken");
        player.sendMessage("§7Linkclicke um die Map zu stellen! (Location = Spawn)");
    }

    private void destroy(LevelPvP plugin) {
        this.points = null;
        plugin.getEventManager().unregisterEvent(PlayerInteractEvent.class, interactevent);
    }
}

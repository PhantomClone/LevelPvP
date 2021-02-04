package me.phantomclone.levelpvp.map;

import me.phantomclone.api.region.Region;
import me.phantomclone.levelpvp.LevelPvP;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MapChangeHandler {

    private final LevelPvP plugin;

    private List<Integer> show;

    private int sectochange, time;

    private Region force;

    private boolean pause;

    public MapChangeHandler(LevelPvP plugin) {
        this.plugin = plugin;
    }

    public void initConfig(String folder) {
        File dir = new File(folder);
        dir.mkdirs();
        File file = new File(dir, "mapchangeconfig.yml");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
                configuration.set("SekundenToChange", 300);
                configuration.set("ShowTime", Arrays.asList(1,2,3,4,5,10,20,30));
                configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.sectochange = configuration.getInt("SekundenToChange");
        this.show = configuration.getIntegerList("ShowTime");
    }

    public void startTimer() {
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            if (pause) return;
            time++;
            plugin.getGuiHandler().getMapChangeGui().updateItems(this);
            if (show.contains(sectochange - time)) {
                if (this.plugin.getMapHandler().getRegionHandler().getRegions().stream().filter(Region::isEnabled).count() < 2) {
                    time = 0;
                    return;
                }
                this.plugin.getServer().broadcastMessage("§8Map wechsel in §d" + (sectochange - time) + " §8Sekunde" + ((sectochange - time) != 1 ? "n" : "")+ "!");
            }
            if (time == sectochange) {
                time = 0;
                Region region = getNextMap();
                if (region != null) {
                    plugin.getMapHandler().getRegion().setLoaded(false);
                    region.setLoaded(true);
                    plugin.getMapHandler().setRegion(region);
                    plugin.getGuiHandler().updateMap();
                    plugin.getServer().getOnlinePlayers().forEach(player -> player.teleport(region.getSpawn()));
                    plugin.getServer().broadcastMessage("§8Neue Map §d" + region.getName());
                    force = null;
                }
            }
        }, 0, 20);
    }

    private Region getNextMap() {
        if (force != null) {
            return force;
        }
        Region current = plugin.getMapHandler().getRegion();
        Object[] objects = plugin.getMapHandler().getRegionHandler().getRegions().stream().filter(region -> region.isEnabled() && !region.getName().equalsIgnoreCase(current.getName())).toArray();
        return (Region) objects[new Random().nextInt(objects.length)];
    }

    public void setForce(Region region) {
        this.force = region;
    }

    public Region getForce() {
        return force;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSectochange() {
        return sectochange;
    }
}

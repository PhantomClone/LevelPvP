package me.phantomclone.levelpvp.level;

import me.phantomclone.api.mysql.MySQL;
import me.phantomclone.api.mysql.queries.*;
import me.phantomclone.eventmanager.EventListener;
import me.phantomclone.levelpvp.LevelPvP;
import me.phantomclone.levelpvp.util.actionbar.ActionBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class LevelHandler {

    private final LevelPvP plugin;
    private MySQL mysql;

    private EventListener<PlayerDeathEvent> deathevent;
    private EventListener<PlayerJoinEvent> joinevent;
    private EventListener<PlayerQuitEvent> quitevent;

    private BukkitTask task;

    private String host, port, user, password, database;
    private double levelcostmultiplicator;
    private int expforlevelone;
    private int expperkill;

    public LevelHandler(LevelPvP plugin) {
        this.plugin = plugin;
        this.mysql = new MySQL();
        initConfig();
        initListener();
    }

    private void initConfig() {
        File folder = new File("./plugins/levelpvp/");
        folder.mkdirs();
        File file = new File(folder, "config.yml");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
                configuration.set("mysql.host", "host");
                configuration.set("mysql.port", "3306");
                configuration.set("mysql.user", "root");
                configuration.set("mysql.password", "password");
                configuration.set("mysql.database", "database");
                configuration.set("levelcostmultiplicator", 1.25);
                configuration.set("expforlevelone", 100);
                configuration.set("expperkill", 25);
                configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.host = configuration.getString("mysql.host");
        this.port = configuration.getString("mysql.port");
        this.user = configuration.getString("mysql.user");
        this.password = configuration.getString("mysql.password");
        this.database = configuration.getString("mysql.database");
        this.levelcostmultiplicator = configuration.getDouble("levelcostmultiplicator");
        this.expforlevelone = configuration.getInt("expforlevelone");
        this.expperkill = configuration.getInt("expperkill");
    }

    private void initListener() {
        deathevent = event -> {
            if (event.getEntity().getKiller() != null && !event.getEntity().equals(event.getEntity().getKiller())) {
                event.getEntity().getKiller().sendMessage("§7Du hast §5" + event.getEntity().getName() + " §7getötet!");
                event.getEntity().sendMessage("§7Du wurdest von §5" + event.getEntity().getKiller().getName() + " §7getötet!");
                addExp(event.getEntity().getKiller());
            }
            event.setDeathMessage("");
        };
        joinevent = event -> loadPlayer(event.getPlayer());
        this.quitevent = event -> {
            if (mysql.getConnectionManager().isClosed()) {
                return;
            }
            try {
                new Query(mysql, new UpdateQuery("levels")
                        .set("level", "" + event.getPlayer().getMetadata("level").get(0).asInt())
                        .set("exp", "" + event.getPlayer().getMetadata("exp").get(0).asInt())
                        .where("uuid='" + event.getPlayer().getUniqueId().toString() + "'")
                        .build()).executeUpdateAsync();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };
        plugin.getEventManager().registerEvent(PlayerDeathEvent.class, deathevent);
        plugin.getEventManager().registerEvent(PlayerJoinEvent.class, joinevent);
        plugin.getEventManager().registerEvent(PlayerQuitEvent.class, quitevent);
    }

    public void loadPlayers() {
        if (!mysql.getConnectionManager().isClosed()) {
            plugin.getServer().getOnlinePlayers().forEach(this::loadPlayer);
        }
    }

    private void loadPlayer(Player player) {
        if (mysql.getConnectionManager().isClosed()) {
            return;
        }
        try {
            new Query(mysql, new SelectQuery("levels").column("*").where("uuid='" + player.getUniqueId().toString() + "'").build()).executeQueryAsync((set, t) -> {
                if (t != null) {
                    t.printStackTrace();
                    return;
                }
                try {
                    if (!set.next()) {
                        Query insert = new Query(mysql, new InsertQuery("levels").value("uuid", "'" + player.getUniqueId().toString() + "'").value("level", "1").value("exp", "0").build());
                        insert.executeUpdate();
                        setMetadata(player, "level", 1);
                        setMetadata(player, "exp", 0);
                    } else {
                        setMetadata(player, "level", set.getInt("level"));
                        setMetadata(player, "exp", set.getInt("exp"));
                    }
                    set.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void startTimer() {
        this.task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (player.hasMetadata("level") && player.hasMetadata("exp")) {
                int level = player.getMetadata("level").get(0).asInt();
                new ActionBar("§5Level - §6" + level + " §8| §dEXP - §a" + player.getMetadata("exp").get(0).asInt() + "§7/§2" + needExp(level)).send(player);
            }
        }), 20, 20);
    }

    private void addExp(Player player) {
        if (player.hasMetadata("level") && player.hasMetadata("exp")) {
            int level = player.getMetadata("level").get(0).asInt();
            int exp = player.getMetadata("exp").get(0).asInt() + expperkill;
            int need = needExp(level);
            if (exp >= Math.round(need)) {
                setMetadata(player, "level", level + 1);
                setMetadata(player, "exp", exp - Math.round(need));
                player.sendMessage("LevelUp");
            } else {
                setMetadata(player, "exp", exp);
            }
        }
    }

    private int needExp(int level) {
        return (int) (this.expforlevelone * Math.pow(this.levelcostmultiplicator, level - 1));
    }

    public void connectToMySQL() {
        if (mysql.connect(this.host, this.port, this.user, this.password, this.database))
        try {
            Query query = new Query(mysql, new CreateTableQuery("levels").ifNotExists()
                    .column("uuid", "VARCHAR(36)")
                    .column("level", "INT")
                    .column("exp", "INT")
                    .primaryKey("uuid")
                    .build());
            query.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } else {
            System.out.println("Es konnte keine MySQL connection aufgebaut werden!");
        }
    }

    public void disconnect() {
        task.cancel();
        plugin.getEventManager().unregisterEvent(PlayerDeathEvent.class, deathevent);
        plugin.getEventManager().unregisterEvent(PlayerJoinEvent.class, joinevent);
        plugin.getEventManager().unregisterEvent(PlayerQuitEvent.class, quitevent);
        if (!mysql.getConnectionManager().isClosed()) {
            mysql.disconnect();
        }
    }

    public void removeMetadata(Player player, String name) {
        if(player.hasMetadata(name))
            player.removeMetadata(name, this.plugin);
    }

    public void setMetadata(Player player, String name, Object value) {
        removeMetadata(player, name);
        player.setMetadata(name, new FixedMetadataValue(this.plugin, value));
    }
}

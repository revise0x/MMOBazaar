package io.github.revise0x.mmobazaar.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.revise0x.mmobazaar.config.StorageConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class StorageFactory {
    private final JavaPlugin plugin;

    public StorageFactory(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public BazaarStorage create(StorageConfig config) {
        switch (config.getEngine()) {
            case SQLITE -> {
                String filePath = config.getSubSection().getString("file", "data.db");
                HikariConfig hikari = new HikariConfig();
                hikari.setJdbcUrl("jdbc:sqlite:" + filePath);
                hikari.setPoolName("MMOBazaar-SQLite");
                hikari.setMaximumPoolSize(10);

                return new SQLiteStorage(plugin, new HikariDataSource(hikari));
            }

            /*case MYSQL, MARIADB -> {
                HikariConfig hikari = config.getHikariConfig();

                return new MySQLStorage(new HikariDataSource(hikari));
            }*/

            default -> {
                Bukkit.getLogger().severe("[MMOBazaar] Unsupported storage engine.");
                return null;
            }
        }
    }
}
package io.github.revise0x.mmobazaar.config;

import com.zaxxer.hikari.HikariConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class StorageConfig {
    public enum Engine {
        SQLITE, MYSQL, MARIADB, POSTGRES;

        public static Engine fromString(String raw) {
            return switch (raw.toLowerCase()) {
                case "sqlite" -> SQLITE;
                case "mysql" -> MYSQL;
                case "mariadb" -> MARIADB;
                case "postgres" -> POSTGRES;
                default -> throw new IllegalArgumentException("Unknown storage engine: " + raw);
            };
        }
    }

    private final Engine engine;
    private final ConfigurationSection section;

    public StorageConfig(ConfigurationSection root) {
        String rawEngine = root.getString("engine", "sqlite");
        this.engine = Engine.fromString(rawEngine);
        this.section = root;
    }

    public Engine getEngine() {
        return engine;
    }

    public ConfigurationSection getSubSection() {
        return section.getConfigurationSection(engine.name().toLowerCase());
    }

    public @NotNull HikariConfig getHikariConfig() {
        var section = getSubSection();
        String host = section.getString("host", "localhost");
        int port = section.getInt("port", 3306);
        String database = section.getString("database", "mmobazaar");
        String user = section.getString("username", "root");
        String pass = section.getString("password", "");
        boolean useSSL = section.getBoolean("useSSL", false);

        HikariConfig hikari = new HikariConfig();
        hikari.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL);
        hikari.setUsername(user);
        hikari.setPassword(pass);
        hikari.setPoolName("MMOBazaar-MySQL");
        hikari.setMaximumPoolSize(10);
        return hikari;
    }
}
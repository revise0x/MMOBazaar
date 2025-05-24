package io.github.revise0x.mmobazaar;

import org.bukkit.configuration.file.FileConfiguration;

public class MMOBazaarConfig {
    private int maxBazaarsPerPlayer;

    public void load(FileConfiguration config) {
        this.maxBazaarsPerPlayer = config.getInt("bazaar.max-per-player");
    }

    public int getMaxBazaarsPerPlayer() {
        return maxBazaarsPerPlayer;
    }
}

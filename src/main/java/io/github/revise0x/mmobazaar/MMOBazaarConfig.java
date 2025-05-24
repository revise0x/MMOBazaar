package io.github.revise0x.mmobazaar;

import org.bukkit.configuration.file.FileConfiguration;

public class MMOBazaarConfig {
    private int maxBazaarsPerPlayer;
    private double creationFee;
    private double extensionFee;

    public void load(FileConfiguration config) {
        this.maxBazaarsPerPlayer = config.getInt("bazaar.max-per-player", 3);
        this.creationFee = config.getDouble("bazaar.creation-fee", 1000.0);
        this.extensionFee = config.getDouble("bazaar.extension-fee", 1000.0);
    }

    public int getMaxBazaarsPerPlayer() {
        return maxBazaarsPerPlayer;
    }

    public double getCreationFee() {
        return creationFee;
    }

    public double getExtensionFee() {
        return extensionFee;
    }

}

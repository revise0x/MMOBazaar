package io.github.revise0x.mmobazaar;

import org.bukkit.plugin.java.JavaPlugin;

public class MMOBazaar extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("MMOBazaar enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MMOBazaar disabled.");
    }
}

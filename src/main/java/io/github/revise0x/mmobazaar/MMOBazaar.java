package io.github.revise0x.mmobazaar;

import io.github.revise0x.mmobazaar.api.MMOBazaarAPI;
import io.github.revise0x.mmobazaar.bazaar.BazaarManager;
import io.github.revise0x.mmobazaar.bazaar.BazaarSpawner;
import io.github.revise0x.mmobazaar.commands.MMOBazaarCommand;
import io.github.revise0x.mmobazaar.gui.BazaarCreateGUI;
import io.github.revise0x.mmobazaar.item.BazaarBagFactory;
import io.github.revise0x.mmobazaar.listener.BazaarUseListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class MMOBazaar extends JavaPlugin {

    @Override
    public void onEnable() {
        BazaarSpawner spawner = new BazaarSpawner();
        BazaarManager bazaarManager = new BazaarManager(spawner);
        
        BazaarCreateGUI createGUI = new BazaarCreateGUI(bazaarManager);

        BazaarBagFactory bagFactory = new BazaarBagFactory();
        MMOBazaarAPI api = new MMOBazaarAPI(bagFactory);

        // Register listener
        getServer().getPluginManager().registerEvents(new BazaarUseListener(bagFactory, createGUI), this);

        // Command
        Objects.requireNonNull(getCommand("mmobazaar")).setExecutor(new MMOBazaarCommand(api));

        getLogger().info("MMOBazaar enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MMOBazaar disabled.");
    }
}

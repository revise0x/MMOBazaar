package io.github.revise0x.mmobazaar;

import io.github.revise0x.mmobazaar.api.MMOBazaarAPI;
import io.github.revise0x.mmobazaar.bazaar.BazaarData;
import io.github.revise0x.mmobazaar.bazaar.BazaarManager;
import io.github.revise0x.mmobazaar.commands.MMOBazaarCommand;
import io.github.revise0x.mmobazaar.config.BazaarConfig;
import io.github.revise0x.mmobazaar.config.StorageConfig;
import io.github.revise0x.mmobazaar.economy.VaultHook;
import io.github.revise0x.mmobazaar.gui.GUISessionManager;
import io.github.revise0x.mmobazaar.item.BazaarBagFactory;
import io.github.revise0x.mmobazaar.listener.BazaarBagUseListener;
import io.github.revise0x.mmobazaar.listener.BazaarInteractionListener;
import io.github.revise0x.mmobazaar.listener.BazaarGUIListener;
import io.github.revise0x.mmobazaar.storage.BazaarStorage;
import io.github.revise0x.mmobazaar.storage.StorageFactory;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Objects;

public class MMOBazaar extends JavaPlugin {
    public static NamespacedKey BAZAAR_ID_KEY;

    @Override
    public void onEnable() {
        BAZAAR_ID_KEY = new NamespacedKey(this, "bazaar-id");

        // Vault Integration
        final VaultHook vaultHook = new VaultHook();
        if (!vaultHook.setup()) {
            getLogger().severe("Vault not found or economy provider missing. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Configuration
        final BazaarConfig config;
        saveDefaultConfig();
        config = new BazaarConfig(getConfig());
        ConfigurationSection storageSection = getConfig().getConfigurationSection("storage");
        if (storageSection == null) {
            getLogger().severe("Storage section not found in config file. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        final StorageConfig storageConfig = new StorageConfig(storageSection);

        // Load storage
        StorageFactory storageFactory = new StorageFactory(this);
        BazaarStorage storage = storageFactory.create(storageConfig);
        if (storage == null) {
            getLogger().severe("Disabling plugin due to missing storage backend.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        storage.init();

        // Setup MMOBazaar
        final BazaarManager bazaarManager = new BazaarManager();
        final BazaarBagFactory bagFactory = new BazaarBagFactory(config.getCreationFee());
        final MMOBazaarAPI api = new MMOBazaarAPI(bagFactory);
        final GUISessionManager guiSessions = new GUISessionManager();

        // Load bazaars from storage
        Collection<BazaarData> loadedBazaars = storage.loadAll();
        getLogger().info("Loaded " + loadedBazaars.size() + " bazaars from database.");
        loadedBazaars.forEach(bazaarManager::registerBazaar);

        // Setup context bundle for easier access to MMOBazaar
        final MMOBazaarContext context = new MMOBazaarContext(this, vaultHook, bazaarManager, bagFactory, api, guiSessions, config);

        // Register listeners
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BazaarBagUseListener(context), this);
        pm.registerEvents(new BazaarInteractionListener(context), this);
        pm.registerEvents(new BazaarGUIListener(context), this);

        // Set command executor
        Objects.requireNonNull(getCommand("mmobazaar")).setExecutor(new MMOBazaarCommand(api));

        getLogger().info("MMOBazaar enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MMOBazaar disabled.");
    }
}

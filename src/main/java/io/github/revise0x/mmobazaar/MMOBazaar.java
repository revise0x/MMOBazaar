package io.github.revise0x.mmobazaar;

import io.github.revise0x.mmobazaar.api.MMOBazaarAPI;
import io.github.revise0x.mmobazaar.bazaar.BazaarManager;
import io.github.revise0x.mmobazaar.commands.MMOBazaarCommand;
import io.github.revise0x.mmobazaar.economy.VaultHook;
import io.github.revise0x.mmobazaar.gui.BazaarCreateGUI;
import io.github.revise0x.mmobazaar.gui.GUISessionManager;
import io.github.revise0x.mmobazaar.item.BazaarBagFactory;
import io.github.revise0x.mmobazaar.listener.BazaarBagUseListener;
import io.github.revise0x.mmobazaar.listener.BazaarInteractionListener;
import io.github.revise0x.mmobazaar.listener.BazaarGUIListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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

        // Setup variables, TODO Change with config in future
        final double creationCost = 1000.0;

        // Setup MMOBazaar
        final BazaarManager bazaarManager = new BazaarManager();
        final BazaarBagFactory bagFactory = new BazaarBagFactory(creationCost);
        final MMOBazaarAPI api = new MMOBazaarAPI(bagFactory);
        final GUISessionManager guiSessions = new GUISessionManager();

        // Setup context bundle for easier access to MMOBazaar
        final MMOBazaarContext context = new MMOBazaarContext(this, vaultHook, bazaarManager, bagFactory, creationCost, api, guiSessions);

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

package io.github.revise0x.mmobazaar;

import io.github.revise0x.mmobazaar.api.MMOBazaarAPI;
import io.github.revise0x.mmobazaar.config.BazaarConfig;
import io.github.revise0x.mmobazaar.economy.VaultHook;
import io.github.revise0x.mmobazaar.bazaar.BazaarManager;
import io.github.revise0x.mmobazaar.gui.GUISessionManager;
import io.github.revise0x.mmobazaar.item.BazaarBagFactory;
import io.github.revise0x.mmobazaar.storage.BazaarStorage;

public class MMOBazaarContext {
    public final MMOBazaar plugin;
    public final VaultHook vaultHook;
    public final BazaarManager bazaarManager;
    public final BazaarBagFactory bagFactory;
    public final MMOBazaarAPI api;
    public final GUISessionManager guiSessions;
    public final BazaarConfig config;
    public final BazaarStorage storage;

    public MMOBazaarContext(MMOBazaar plugin, VaultHook vaultHook, BazaarManager manager, BazaarBagFactory bagFactory, MMOBazaarAPI api, GUISessionManager guiSessions, BazaarConfig config, BazaarStorage storage) {
        this.plugin = plugin;
        this.vaultHook = vaultHook;
        this.bazaarManager = manager;
        this.bagFactory = bagFactory;
        this.api = api;
        this.guiSessions = guiSessions;
        this.config = config;
        this.storage = storage;
    }
}

package io.github.revise0x.mmobazaar;

import io.github.revise0x.mmobazaar.api.MMOBazaarAPI;
import io.github.revise0x.mmobazaar.economy.VaultHook;
import io.github.revise0x.mmobazaar.bazaar.BazaarManager;
import io.github.revise0x.mmobazaar.gui.GUISessionManager;
import io.github.revise0x.mmobazaar.item.BazaarBagFactory;

public class MMOBazaarContext {
    public final MMOBazaar plugin;
    public final VaultHook vaultHook;
    public final BazaarManager bazaarManager;
    public final BazaarBagFactory bagFactory;
    public final double creationCost;
    public final MMOBazaarAPI api;
    public final GUISessionManager guiSessions;
    public final MMOBazaarConfig config;

    public MMOBazaarContext(MMOBazaar plugin, VaultHook vaultHook, BazaarManager manager, BazaarBagFactory bagFactory, double creationCost, MMOBazaarAPI api, GUISessionManager guiSessions, MMOBazaarConfig config) {
        this.plugin = plugin;
        this.vaultHook = vaultHook;
        this.bazaarManager = manager;
        this.bagFactory = bagFactory;
        this.creationCost = creationCost;
        this.api = api;
        this.guiSessions = guiSessions;
        this.config = config;
    }
}

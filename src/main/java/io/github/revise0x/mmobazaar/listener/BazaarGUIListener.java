package io.github.revise0x.mmobazaar.listener;

import io.github.revise0x.mmobazaar.MMOBazaarContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class BazaarGUIListener implements Listener {
    private final MMOBazaarContext context;

    public BazaarGUIListener(MMOBazaarContext context) {
        this.context = context;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Check if this player has an active owner GUI open
        context.guiSessions.getOwnerGUI(player.getUniqueId()).ifPresent(ownerGUI -> ownerGUI.handleClick(player, event));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        context.guiSessions.removeOwnerGUI(event.getPlayer().getUniqueId());
    }
}
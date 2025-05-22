package io.github.revise0x.mmobazaar.listener;

import io.github.revise0x.mmobazaar.gui.BazaarCreateGUI;
import io.github.revise0x.mmobazaar.item.BazaarBagFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BazaarUseListener implements Listener {
    private final BazaarBagFactory bagFactory;
    private final BazaarCreateGUI createGUI;

    public BazaarUseListener(BazaarBagFactory bagFactory, BazaarCreateGUI createGUI) {
        this.bagFactory = bagFactory;
        this.createGUI = createGUI;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        // Ignore offhand and empty interacts
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;

        Player player = event.getPlayer();

        if (bagFactory.isBazaarBag(item)) {
            event.setCancelled(true); // Cancel in case player does actually interact with something

            // Use 1 of the item
            item.setAmount(item.getAmount() - 1);
            player.sendMessage("§e[MMOBazaar] Starting market setup...");
            createGUI.open(player);
        }
    }
}

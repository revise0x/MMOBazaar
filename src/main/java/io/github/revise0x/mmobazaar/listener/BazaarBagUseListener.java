package io.github.revise0x.mmobazaar.listener;

import io.github.revise0x.mmobazaar.MMOBazaarContext;
import io.github.revise0x.mmobazaar.gui.BazaarCreateGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BazaarBagUseListener implements Listener {
    private final MMOBazaarContext context;
    private final BazaarCreateGUI createGUI;

    public BazaarBagUseListener(MMOBazaarContext context, BazaarCreateGUI createGUI) {
        this.context = context;
        this.createGUI = createGUI;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        // Ignore offhand and empty interacts
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;

        Player player = event.getPlayer();

        if (context.bagFactory.isBazaarBag(item)) {
            event.setCancelled(true); // Cancel in case player does actually interact with something

            double balance = context.vaultHook.getEconomy().getBalance(player);
            if (balance < context.creationCost) {
                player.sendMessage("§cYou need at least §f$" + context.creationCost + " §cto open a bazaar.");
                return;
            }

            // Use 1 of the item
            item.setAmount(item.getAmount() - 1);

            player.sendMessage("§e[MMOBazaar] Starting market setup...");
            createGUI.open(player);
        }
    }
}

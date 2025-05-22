package io.github.revise0x.mmobazaar.gui;

import io.github.revise0x.mmobazaar.MMOBazaarContext;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BazaarCreateGUI {
    private final MMOBazaarContext context;

    public BazaarCreateGUI(MMOBazaarContext context) {
        this.context = context;
    }

    public void open(Player player) {
        new AnvilGUI.Builder()
                .onClose((stateSnapshot) -> {
                    if (stateSnapshot.getText() == null || stateSnapshot.getText().trim().isEmpty()) {
                        player.sendMessage("§cBazaar name cannot be empty.");
                    }

                    boolean created = context.bazaarManager.createBazaar(player, stateSnapshot.getText().trim());
                    if (created) {
                        context.vaultHook.getEconomy().withdrawPlayer(player, context.creationCost);
                        player.sendMessage("§aBazaar created and §f$" + context.creationCost + " §awithdrawn.");
                    } else {
                        player.sendMessage("§cFailed to create bazaar.");
                        refundBag(player);
                    }
                })
                .text("Enter bazaar name")
                .itemLeft(new ItemStack(Material.NAME_TAG))
                .title("Bazaar Name")
                .plugin(org.bukkit.Bukkit.getPluginManager().getPlugin("MMOBazaar"))
                .open(player);
    }

    private void refundBag(Player player) {
        ItemStack bag = context.bagFactory.create();
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(bag);

        if (!leftover.isEmpty()) {
            // If inventory full, drop the item at player location
            for (ItemStack item : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
            player.sendMessage("§eYour bazaar bag was dropped because your inventory is full.");
        } else {
            player.sendMessage("§eYour bazaar bag has been returned.");
        }
    }
}
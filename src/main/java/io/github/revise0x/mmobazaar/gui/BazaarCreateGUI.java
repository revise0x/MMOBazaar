package io.github.revise0x.mmobazaar.gui;

import io.github.revise0x.mmobazaar.MMOBazaarContext;
import io.github.revise0x.mmobazaar.bazaar.BazaarData;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BazaarCreateGUI {
    private final MMOBazaarContext context;

    public BazaarCreateGUI(MMOBazaarContext context) {
        this.context = context;
    }

    public void open(Player player) {
        new AnvilGUI.Builder().plugin(context.plugin).title("Bazaar Name").text("Enter name").itemLeft(new ItemStack(Material.NAME_TAG)).onClick((slot, state) -> {
            if (slot != AnvilGUI.Slot.OUTPUT) return List.of(); // only act on confirmation

            String name = state.getText().trim();
            if (name.isEmpty()) {
                player.sendMessage("§cBazaar name cannot be empty.");
                refundBag(player);
                return List.of(AnvilGUI.ResponseAction.close());
            }

            return context.bazaarManager.createBazaar(player, name).map(data -> List.of(AnvilGUI.ResponseAction.close(), AnvilGUI.ResponseAction.run(() -> {
                context.vaultHook.getEconomy().withdrawPlayer(player, context.creationCost);
                player.sendMessage("§aBazaar created and §f$" + context.creationCost + " §awithdrawn.");
                new BazaarOwnerGUI(context, data).open(player);
            }))).orElseGet(() -> {
                player.sendMessage("§cFailed to create bazaar.");
                refundBag(player);
                return List.of(AnvilGUI.ResponseAction.close());
            });
        }).open(player);
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
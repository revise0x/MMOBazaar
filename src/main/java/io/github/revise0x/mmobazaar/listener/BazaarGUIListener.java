package io.github.revise0x.mmobazaar.listener;

import io.github.revise0x.mmobazaar.MMOBazaarContext;
import io.github.revise0x.mmobazaar.bazaar.BazaarData;
import io.github.revise0x.mmobazaar.bazaar.BazaarListing;
import io.github.revise0x.mmobazaar.gui.BazaarOwnerGUI;
import io.github.revise0x.mmobazaar.gui.ConfirmPurchaseGUI;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class BazaarGUIListener implements Listener {
    private final MMOBazaarContext context;

    public BazaarGUIListener(MMOBazaarContext context) {
        this.context = context;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        context.guiSessions.getConfirming(player.getUniqueId()).ifPresent(gui -> {
            event.setCancelled(true);
            int slot = event.getRawSlot();

            switch (slot) {
                case 3 -> { // Confirm purchase
                    double price = gui.getListing().getPrice();
                    if (!context.vaultHook.getEconomy().has(player, price)) {
                        player.sendMessage("§cYou don’t have enough money.");
                        player.closeInventory();
                        context.guiSessions.removeConfirming(player.getUniqueId());
                        return;
                    }

                    ItemStack item = gui.getListing().getItem().clone();
                    HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
                    if (!leftover.isEmpty()) {
                        player.sendMessage("§cNot enough inventory space.");
                        player.closeInventory();
                        context.guiSessions.removeConfirming(player.getUniqueId());
                        return;
                    }

                    BazaarData data = gui.getData();
                    BazaarListing current = data.getListings().get(gui.getSlot());
                    if (current == null || !current.getItem().isSimilar(gui.getListing().getItem())) {
                        player.sendMessage("§cThis item is no longer available.");
                        player.closeInventory();
                        context.guiSessions.removeConfirming(player.getUniqueId());
                        return;
                    }

                    // 1. Remove listing
                    data.removeListing(gui.getSlot());

                    // 2. Vault transfer
                    context.vaultHook.getEconomy().withdrawPlayer(player, price);
                    data.deposit(price);

                    // 3. Notify
                    player.sendMessage("§aYou bought the item for §f$" + price + "§a.");
                    Player owner = Bukkit.getPlayer(data.getOwner());
                    if (owner != null) owner.sendMessage("§aSomeone bought an item from your bazaar.");

                    // 4. Close and cleanup
                    player.closeInventory();
                    context.guiSessions.removeConfirming(player.getUniqueId());
                }

                case 5 -> { // Cancel
                    player.sendMessage("§7Purchase cancelled.");
                    player.closeInventory();
                    context.guiSessions.removeConfirming(player.getUniqueId());
                }
            }
        });
        context.guiSessions.getCustomerGUI(player.getUniqueId()).ifPresent(gui -> {
            event.setCancelled(true);

            int slot = event.getRawSlot();
            BazaarListing listing = gui.data().getListings().get(slot);
            if (listing == null) return;

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType().isAir()) return;

            ItemStack original = listing.getItem();

            // Security check: must match listing item
            if (!clicked.isSimilar(original)) {
                player.sendMessage("§cThis item does not match the listing. Please reopen the shop.");
                player.closeInventory();
                context.guiSessions.removeCustomerGUI(player.getUniqueId());
                return;
            }

            // Open confirmation GUI
            new ConfirmPurchaseGUI(context, gui.data(), listing, slot).open(player);
        });
        context.guiSessions.getOwnerGUI(player.getUniqueId()).ifPresent(gui -> {
            Inventory clicked = event.getClickedInventory();
            if (clicked == null) return;

            // Soft check in case another inventory event
            if (ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase(gui.getData().getName())) {
                context.guiSessions.removeOwnerGUI(player.getUniqueId());
                return;
            }

            int slot = event.getRawSlot();

            // Modify listings: between 0–26 slots
            if (slot >= 0 && slot <= 26 && clicked.equals(event.getView().getTopInventory())) {
                ItemStack cursor = event.getCursor();
                if (cursor != null && !cursor.getType().isAir()) {
                    event.setCancelled(true);

                    ItemStack droppedItem = cursor.clone();
                    player.setItemOnCursor(null); // Remove item on cursor to prevent duplication

                    openListingPrompt(player, droppedItem, slot, gui);
                    return;
                }

                // If there is an item in slot: Edit Price or Remove Listing
                BazaarListing existing = gui.getData().getListings().get(slot);
                if (existing != null) {
                    event.setCancelled(true);

                    if (event.getClick().isLeftClick()) {
                        openEditPrompt(player, slot, existing, gui);
                    } else if (event.getClick().isRightClick()) {
                        gui.getData().removeListing(slot);
                        player.sendMessage("§cListing removed.");

                        HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(existing.getItem());
                        for (ItemStack item : leftovers.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                        }

                        gui.open(player); // Refresh GUI
                    }
                    return;
                }
            }

            // Handle other slot clicks for button controls
            gui.handleClick(player, event);
        });
    }

    private void openListingPrompt(Player player, ItemStack item, int slot, BazaarOwnerGUI gui) {
        new AnvilGUI.Builder().onClick((_s, stateSnapshot) -> {
            try {
                double price = Double.parseDouble(stateSnapshot.getText());
                if (price <= 0) throw new NumberFormatException();

                gui.getData().addListing(slot, item, price);
                player.sendMessage("§aItem listed for §f$" + price);
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid price.");
                // Return item
                HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
                for (ItemStack leftover : leftovers.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                }
            }

            return List.of(AnvilGUI.ResponseAction.close(), AnvilGUI.ResponseAction.run(() -> gui.open(player)));
        }).text("10.0").itemLeft(new ItemStack(Material.NAME_TAG)).title("Enter Price").plugin(context.plugin).open(player);
    }

    private void openEditPrompt(Player player, int slot, BazaarListing listing, BazaarOwnerGUI gui) {
        new AnvilGUI.Builder().plugin(context.plugin).title("Edit Price").text(String.valueOf(listing.getPrice())).itemLeft(new ItemStack(Material.NAME_TAG)).onClick((clickedSlot, state) -> {
            if (clickedSlot != AnvilGUI.Slot.OUTPUT) return List.of();

            try {
                double newPrice = Double.parseDouble(state.getText());
                if (newPrice <= 0) throw new NumberFormatException();

                boolean updated = gui.getData().changeListingPrice(slot, newPrice);
                if (updated) {
                    player.sendMessage("§aPrice updated to §f$" + newPrice);
                } else {
                    player.sendMessage("§cFailed to update price: listing not found.");
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid price.");
            }

            return List.of(AnvilGUI.ResponseAction.close(), AnvilGUI.ResponseAction.run(() -> gui.open(player)));
        }).open(player);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        context.guiSessions.removeOwnerGUI(event.getPlayer().getUniqueId());
        context.guiSessions.removeCustomerGUI(event.getPlayer().getUniqueId());
    }
}
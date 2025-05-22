package io.github.revise0x.mmobazaar.gui;

import io.github.revise0x.mmobazaar.bazaar.BazaarData;
import io.github.revise0x.mmobazaar.MMOBazaarContext;
import io.github.revise0x.mmobazaar.bazaar.BazaarListing;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class BazaarOwnerGUI {
    private final MMOBazaarContext context;
    private final BazaarData data;

    public BazaarOwnerGUI(MMOBazaarContext context, BazaarData data) {
        this.context = context;
        this.data = data;
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, data.getName());

        // Listings 0–26
        for (int slot = 0; slot <= 26; slot++) {
            if (data.getListings().containsKey(slot)) {
                gui.setItem(slot, data.getListings().get(slot).getItem());
            }
        }

        // Filler glass panes on final row
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
        }
        for (int i = 27; i < 36; i++) {
            gui.setItem(i, glass);
        }

        // Buttons
        gui.setItem(30, makeButton(Material.GOLD_INGOT, "§6Withdraw Earnings", "§7Click to withdraw all money"));
        gui.setItem(31, makeButton(Material.BARRIER, "§cClose Bazaar", "§7Removes the bazaar and refunds items"));
        long millisLeft = data.getExpiresAt() - System.currentTimeMillis();
        gui.setItem(32, makeButton(Material.CLOCK, "§eTime Left: " + formatTime(millisLeft), "§7Click to extend by 1 day for $1000"));
        gui.setItem(35, makeButton(Material.COMPASS, "§bRotate Bazaar", "§7Click to rotate stand 15°"));

        player.openInventory(gui);
    }

    public void handleClick(Player player, InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getRawSlot();

        switch (slot) {
            case 30 -> {
                double withdrawn = data.withdrawAll();
                context.vaultHook.getEconomy().depositPlayer(player, withdrawn);
                player.sendMessage("§aWithdrawn §f$" + withdrawn + "§a to your balance.");
                open(player);
            }
            case 31 -> {
                // 1. Refund items
                for (BazaarListing listing : data.getListings().values()) {
                    HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(listing.getItem());
                    for (ItemStack item : leftovers.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                    }
                }

                // 2. Refund bank
                double withdrawn = data.withdrawAll();
                context.vaultHook.getEconomy().depositPlayer(player, withdrawn);

                // 3. Remove stand & unregister
                context.bazaarManager.removeBazaar(data.getId());

                // 4. Notify
                player.sendMessage("§cYour bazaar has been closed.");
                player.sendMessage("§7Items and §f$" + withdrawn + "§7 returned.");

                // 5. Close inventory
                player.closeInventory();

                // 6. Clear GUI session
                context.guiSessions.removeOwnerGUI(player.getUniqueId());
            }
            case 32 -> {
                boolean extended = data.extendExpiration(86400000);
                if (extended) {
                    context.vaultHook.getEconomy().withdrawPlayer(player, 1000.0);
                    player.sendMessage("§aExtended bazaar by 1 day for §f$1000.");
                } else {
                    player.sendMessage("§eYou can't extend beyond 2 days from now.");
                }
                open(player);
            }
            case 35 -> {
                if (context.bazaarManager.rotateBazaar(data, 15.0f)) player.sendMessage("§bBazaar rotated!");
            }
        }
    }

    private ItemStack makeButton(Material mat, String name, String... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of(loreLines));
            item.setItemMeta(meta);
        }
        return item;
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = (seconds / 60) % 60;
        long hours = (seconds / 3600) % 24;
        long days = seconds / 86400;
        return days + "d " + hours + "h " + minutes + "m";
    }
}

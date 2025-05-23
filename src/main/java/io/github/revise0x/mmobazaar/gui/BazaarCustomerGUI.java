package io.github.revise0x.mmobazaar.gui;

import io.github.revise0x.mmobazaar.bazaar.BazaarData;
import io.github.revise0x.mmobazaar.bazaar.BazaarListing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record BazaarCustomerGUI(BazaarData data) {
    public void open(Player player) {
        if (data.isClosed()) {
            player.sendMessage("§cThis bazaar is currently closed.");
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GRAY + data.getName());

        for (Map.Entry<Integer, BazaarListing> entry : data.getListings().entrySet()) {
            int slot = entry.getKey();
            BazaarListing listing = entry.getValue();

            ItemStack display = listing.getItem().clone();
            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.hasLore() && meta.getLore() != null
                        ? new ArrayList<>(meta.getLore())
                        : new ArrayList<>();
                if (!lore.isEmpty()) {
                    lore.add(""); // spacer
                }
                lore.add("§7Price: §f$" + listing.getPrice());
                lore.add("§7Seller: §f" + Bukkit.getOfflinePlayer(data.getOwner()).getName());
                lore.add("§eClick to buy");
                meta.setLore(lore);
                display.setItemMeta(meta);
            }

            gui.setItem(slot, display);
        }

        player.openInventory(gui);
    }
}
package io.github.revise0x.mmobazaar.gui;

import io.github.revise0x.mmobazaar.MMOBazaarContext;
import io.github.revise0x.mmobazaar.bazaar.BazaarData;
import io.github.revise0x.mmobazaar.bazaar.BazaarListing;
import io.github.revise0x.mmobazaar.util.ListingLoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BazaarCustomerGUI {
    private final MMOBazaarContext context;
    private final BazaarData data;

    public BazaarCustomerGUI(MMOBazaarContext context, BazaarData data) {
        this.context = context;
        this.data = data;
    }

    public void open(Player player) {
        if (data.isClosed()) {
            player.sendMessage("§cThis bazaar is currently closed.");
            return;
        }

        // Register session for event handling
        context.guiSessions.setCustomerGUI(player.getUniqueId(), this);

        Inventory gui = Bukkit.createInventory(null, 27, data.getName());

        for (Map.Entry<Integer, BazaarListing> entry : data.getListings().entrySet()) {
            int slot = entry.getKey();
            BazaarListing listing = entry.getValue();

            gui.setItem(slot, ListingLoreUtil.withBazaarLore(listing.getItem().clone(), listing.getPrice(), Bukkit.getOfflinePlayer(data.getOwner()).getName()));
        }

        player.openInventory(gui);
    }

    public BazaarData getData() {
        return data;
    }
}
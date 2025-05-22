package io.github.revise0x.mmobazaar.bazaar;

import org.bukkit.inventory.ItemStack;

public class BazaarListing {
    private final ItemStack item;
    private final double price;
    private final long timestamp; // when listed, for sorting later

    public BazaarListing(ItemStack item, double price) {
        this.item = item;
        this.price = price;
        this.timestamp = System.currentTimeMillis();
    }

    public ItemStack getItem() {
        return item;
    }

    public double getPrice() {
        return price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BazaarListing withPrice(double newPrice) {
        return new BazaarListing(item, newPrice);
    }
}

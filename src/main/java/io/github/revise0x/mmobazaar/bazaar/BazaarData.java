package io.github.revise0x.mmobazaar.bazaar;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BazaarData {
    private final UUID id;
    private final UUID owner;
    private final String name;
    private final Location location;
    private final long createdAt;
    private long expiresAt; // mutable: extendable by owner
    private double bankBalance = 0.0;
    private boolean closed = false;

    private final Map<Integer, BazaarListing> listings = new HashMap<>();

    public BazaarData(UUID id, UUID owner, String name, Location location) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.location = location;
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = this.createdAt + TimeUnit.DAYS.toMillis(1); // default 1 day
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public boolean extendExpiration(long millis) {
        long now = System.currentTimeMillis();
        long maxExpiresAt = now + TimeUnit.DAYS.toMillis(2);
        long proposed = expiresAt + millis;

        if (proposed > maxExpiresAt) {
            return false; // can't stack more than 2 days from now
        }

        expiresAt = proposed;
        return true;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public double getBankBalance() {
        return bankBalance;
    }

    public void deposit(double amount) {
        bankBalance += amount;
    }

    public double withdrawAll() {
        double withdrawn = bankBalance;
        bankBalance = 0.0;
        return withdrawn;
    }

    public Map<Integer, BazaarListing> getListings() {
        return listings;
    }

    public void addListing(int slot, ItemStack item, double price) {
        listings.put(slot, new BazaarListing(item, price));
    }

    public void removeListing(int slot) {
        listings.remove(slot);
    }

    public boolean changeListingPrice(int slot, double newPrice) {
        BazaarListing current = listings.get(slot);
        if (current == null) return false;

        listings.put(slot, new BazaarListing(current.getItem(), newPrice));
        return true;
    }
}

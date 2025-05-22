package io.github.revise0x.mmobazaar.bazaar;

import org.bukkit.Location;

import java.util.UUID;

public class BazaarData {
    private final UUID owner;
    private final String name;
    private final Location location;

    public BazaarData(UUID owner, String name, Location location) {
        this.owner = owner;
        this.name = name;
        this.location = location;
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

    // TODO income, inventory, uuid, creationDate...
}

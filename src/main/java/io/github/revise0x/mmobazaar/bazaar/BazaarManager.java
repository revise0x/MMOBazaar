package io.github.revise0x.mmobazaar.bazaar;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class BazaarManager {
    private final BazaarSpawner spawner;
    private final Map<UUID, BazaarData> bazaars = new HashMap<>();

    public BazaarManager(BazaarSpawner spawner) {
        this.spawner = spawner;
    }

    public boolean createBazaar(Player player, String name) {
        if (isTooClose(player.getLocation(), 2.5)) {
            player.sendMessage("§cYou are too close to another bazaar.");
            return false;
        }

        BazaarData data = new BazaarData(player.getUniqueId(), name, player.getLocation());
        bazaars.put(player.getUniqueId(), data);
        spawner.spawn(player, name);

        player.sendMessage("§aBazaar created: §f" + name);
        return true;
    }

    public boolean isTooClose(Location location, double radius) {
        for (BazaarData data : bazaars.values()) {
            if (Objects.equals(data.getLocation().getWorld(), location.getWorld()) && data.getLocation().distanceSquared(location) < radius * radius) {
                return true;
            }
        }
        return false;
    }

    public Optional<BazaarData> getBazaar(UUID owner) {
        return Optional.ofNullable(bazaars.get(owner));
    }
}
package io.github.revise0x.mmobazaar.bazaar;

import io.github.revise0x.mmobazaar.MMOBazaar;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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

        BazaarData data = new BazaarData(UUID.randomUUID(), player.getUniqueId(), name, player.getLocation());
        bazaars.put(data.getId(), data); // Registers bazaar
        spawner.spawn(data);

        player.sendMessage("§aBazaar created: §f" + name);
        return true;
    }

    public boolean rotateBazaar(BazaarData data, float amount) {
        return getArmorStandForBazaar(data).map(stand -> {
            Location loc = stand.getLocation();
            float newYaw = loc.getYaw() + amount;
            if (newYaw >= 360.0f) newYaw -= 360.0f;

            loc.setYaw(newYaw);
            stand.teleport(loc);
            return true;
        }).orElse(false);
    }

    public boolean removeBazaar(UUID bazaarId) {
        BazaarData data = bazaars.remove(bazaarId);
        if (data == null) return false;

        // Remove ArmorStand in world
        getArmorStandForBazaar(data).ifPresent(Entity::remove);

        return true;
    }

    public BazaarData getBazaar(UUID id) {
        return bazaars.get(id);
    }

    public boolean isTooClose(Location location, double radius) {
        for (BazaarData data : bazaars.values()) {
            if (Objects.equals(data.getLocation().getWorld(), location.getWorld()) && data.getLocation().distanceSquared(location) < radius * radius) {
                return true;
            }
        }
        return false;
    }

    public Optional<ArmorStand> getArmorStandForBazaar(BazaarData data) {
        Location loc = data.getLocation();
        World world = loc.getWorld();
        if (world == null) return Optional.empty();

        for (Entity entity : world.getNearbyEntities(loc, 1, 1, 1)) {
            if (entity instanceof ArmorStand stand) {
                PersistentDataContainer pdc = stand.getPersistentDataContainer();
                String raw = pdc.get(MMOBazaar.BAZAAR_ID_KEY, PersistentDataType.STRING);
                if (raw != null && raw.equals(data.getId().toString())) {
                    return Optional.of(stand);
                }
            }
        }

        return Optional.empty();
    }
}
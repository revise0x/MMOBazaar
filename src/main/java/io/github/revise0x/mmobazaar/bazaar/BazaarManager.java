package io.github.revise0x.mmobazaar.bazaar;

import io.github.revise0x.mmobazaar.MMOBazaar;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;

import java.util.*;

public class BazaarManager {
    private final Map<UUID, BazaarData> bazaars = new HashMap<>();

    public Optional<BazaarData> createBazaar(Player player, String name) {
        BazaarData data = new BazaarData(UUID.randomUUID(), player.getUniqueId(), name, player.getLocation());
        if (!spawnBazaar(data)) {
            player.sendMessage("§cYou are too close to another bazaar.");
            return Optional.empty();
        }

        // Registers bazaar
        bazaars.put(data.getId(), data);

        player.sendMessage("§aBazaar created: §f" + name);
        return Optional.of(data);
    }

    public void removeBazaar(UUID bazaarId) {
        BazaarData data = bazaars.remove(bazaarId);
        if (data == null) return;

        getArmorStandForBazaar(data).ifPresent(stand -> {
            String id = data.getId().toString();
            stand.remove();

            World world = stand.getWorld();
            world.getNearbyEntities(stand.getLocation().clone().add(0, 1.2, 0), 0.5, 0.5, 0.5).stream().filter(e -> e instanceof ArmorStand).map(e -> (ArmorStand) e).filter(holo -> id.equals(holo.getPersistentDataContainer().get(MMOBazaar.BAZAAR_ID_KEY, PersistentDataType.STRING))).forEach(Entity::remove);
        });
    }

    public BazaarData getBazaar(UUID id) {
        return bazaars.get(id);
    }

    private boolean spawnBazaar(BazaarData data) {
        Location baseLoc = data.getLocation().getBlock().getLocation().add(Math.random() * 0.4 + 0.3, 0, Math.random() * 0.4 + 0.3);
        World world = baseLoc.getWorld();
        if (world == null) return false;

        if (isTooClose(data.getLocation(), 2.5)) return false;

        // 1. Armor Stand
        ArmorStand stand = world.spawn(baseLoc.clone(), ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setBasePlate(false);
        stand.setInvulnerable(true);
        Objects.requireNonNull(stand.getEquipment()).setHelmet(new ItemStack(Material.CHEST));
        stand.setHeadPose(new EulerAngle(0, 0, 0));
        stand.getPersistentDataContainer().set(MMOBazaar.BAZAAR_ID_KEY, PersistentDataType.STRING, data.getId().toString());

        // 2. Hologram 1 – Bazaar name
        ArmorStand nameLine = world.spawn(baseLoc.clone().add(0, 1.15, 0), ArmorStand.class);
        nameLine.setCustomName("§6" + data.getName());
        nameLine.setCustomNameVisible(true);
        nameLine.setVisible(false);
        nameLine.setGravity(false);
        nameLine.setMarker(true);
        nameLine.setInvulnerable(true);
        nameLine.getPersistentDataContainer().set(MMOBazaar.BAZAAR_ID_KEY, PersistentDataType.STRING, data.getId().toString());

        // 3. Hologram 2 – Owner
        ArmorStand ownerLine = world.spawn(baseLoc.clone().add(0, 1.00, 0), ArmorStand.class);
        ownerLine.setCustomName("§7" + Bukkit.getOfflinePlayer(data.getOwner()).getName());
        ownerLine.setCustomNameVisible(true);
        ownerLine.setVisible(false);
        ownerLine.setGravity(false);
        ownerLine.setMarker(true);
        ownerLine.setInvulnerable(true);
        ownerLine.getPersistentDataContainer().set(MMOBazaar.BAZAAR_ID_KEY, PersistentDataType.STRING, data.getId().toString());

        return true;
    }

    public void updateVisual(BazaarData data) {
        getArmorStandForBazaar(data).ifPresent(stand -> {
            String id = data.getId().toString();
            World world = stand.getWorld();

            world.getNearbyEntities(stand.getLocation().clone().add(0, 1.2, 0), 0.5, 0.5, 0.5).stream().filter(e -> e instanceof ArmorStand).map(e -> (ArmorStand) e).filter(holo -> {
                String match = holo.getPersistentDataContainer().get(MMOBazaar.BAZAAR_ID_KEY, PersistentDataType.STRING);
                return id.equals(match) && !holo.equals(stand); // avoid changing main stand
            }).forEach(holo -> {
                String prefix = data.isClosed() ? "§c[CLOSED] §6" : "§6";
                holo.setCustomName(prefix + data.getName());
            });
        });
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
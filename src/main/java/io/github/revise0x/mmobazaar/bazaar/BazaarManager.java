package io.github.revise0x.mmobazaar.bazaar;

import io.github.revise0x.mmobazaar.MMOBazaar;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
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

        getEntity(data.getVisualStandId()).ifPresent(Entity::remove);
        getEntity(data.getOwnerStandId()).ifPresent(Entity::remove);
        getEntity(data.getNameStandId()).ifPresent(Entity::remove);
    }

    public BazaarData getBazaar(UUID id) {
        return bazaars.get(id);
    }

    private boolean spawnBazaar(BazaarData data) {
        Location baseLoc = data.getLocation().getBlock().getLocation().add(Math.random() * 0.4 + 0.3, 0, Math.random() * 0.4 + 0.3);
        World world = baseLoc.getWorld();
        if (world == null) return false;

        if (isTooClose(data.getLocation(), 2.5)) return false;

        // 1. Bazaar
        ArmorStand stand = world.spawn(baseLoc.clone().add(0, -1.25, 0), ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMarker(false);
        stand.setBasePlate(false);
        stand.setInvulnerable(true);
        Objects.requireNonNull(stand.getEquipment()).setHelmet(new ItemStack(Material.CHEST));
        stand.setHeadPose(new EulerAngle(0, 0, 0));
        stand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        stand.getPersistentDataContainer().set(MMOBazaar.BAZAAR_ID_KEY, PersistentDataType.STRING, data.getId().toString());
        data.setNameStandId(stand.getUniqueId());

        // 2. Hologram 1 – Bazaar name
        ArmorStand nameLine = world.spawn(baseLoc.clone().add(0, 1.20, 0), ArmorStand.class);
        nameLine.setCustomName("§6" + data.getName());
        nameLine.setCustomNameVisible(true);
        nameLine.setVisible(false);
        nameLine.setGravity(false);
        nameLine.setMarker(true);
        nameLine.setInvulnerable(true);
        nameLine.getPersistentDataContainer().set(MMOBazaar.BAZAAR_ID_KEY, PersistentDataType.STRING, data.getId().toString());
        data.setNameStandId(nameLine.getUniqueId());

        // 3. Hologram 2 – Owner
        ArmorStand ownerLine = world.spawn(baseLoc.clone().add(0, 1, 0), ArmorStand.class);
        ownerLine.setCustomName("§7" + Bukkit.getOfflinePlayer(data.getOwner()).getName());
        ownerLine.setCustomNameVisible(true);
        ownerLine.setVisible(false);
        ownerLine.setGravity(false);
        ownerLine.setMarker(true);
        ownerLine.setInvulnerable(true);
        ownerLine.getPersistentDataContainer().set(MMOBazaar.BAZAAR_ID_KEY, PersistentDataType.STRING, data.getId().toString());
        data.setOwnerStandId(ownerLine.getUniqueId());

        return true;
    }

    public Optional<Entity> getEntity(UUID id) {
        return Optional.ofNullable(Bukkit.getEntity(id));
    }

    public void updateBazaarDisplayName(BazaarData data) {
        getEntity(data.getNameStandId()).ifPresent(entity -> {
            String prefix = data.isClosed() ? "§c[CLOSED] §6" : "§6";
            entity.setCustomName(prefix + data.getName());
        });
    }

    public boolean rotateBazaar(BazaarData data, float amount) {
        return getEntity(data.getVisualStandId()).map(stand -> {
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
}
package io.github.revise0x.mmobazaar.bazaar;

import io.github.revise0x.mmobazaar.MMOBazaar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;

import java.util.Objects;

public class BazaarSpawner {
    public BazaarSpawner() {
        // TODO config, database, language loader data
    }

    public void spawn(BazaarData data) {
        // TODO Disable spawning bazaars to close to other bazaars.
        Location baseLoc = data.getLocation().getBlock().getLocation().add(Math.random() * 0.4 + 0.3, 0, Math.random() * 0.4 + 0.3);
        World world = baseLoc.getWorld();
        if (world == null) return;

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

        // 3. Hologram 2 – Owner
        ArmorStand ownerLine = world.spawn(baseLoc.clone().add(0, 1.00, 0), ArmorStand.class);
        ownerLine.setCustomName("§7" + Bukkit.getOfflinePlayer(data.getOwner()).getName());
        ownerLine.setCustomNameVisible(true);
        ownerLine.setVisible(false);
        ownerLine.setGravity(false);
        ownerLine.setMarker(true);
        ownerLine.setInvulnerable(true);
    }
}

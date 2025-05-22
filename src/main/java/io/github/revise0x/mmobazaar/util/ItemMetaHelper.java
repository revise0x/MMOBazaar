package io.github.revise0x.mmobazaar.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.Optional;

public class ItemMetaHelper {
    private static final int currentVersion = getMajorVersion();

    private static int getMajorVersion() {
        String version = Bukkit.getBukkitVersion(); // e.g. "1.21.4-R0.1-SNAPSHOT"
        try {
            String[] split = version.split("\\.");
            return Integer.parseInt(split[1]);
        } catch (Exception e) {
            return 0; // fallback
        }
    }

    public static Optional<Integer> getCustomModelData(ItemMeta meta) {
        if (meta == null) return Optional.empty();

        if (currentVersion >= 21) {
            try {
                Class<?> componentClass = Class.forName("org.bukkit.inventory.meta.CustomModelDataComponent");
                Method getComponent = meta.getClass().getMethod("getCustomModelDataComponent");
                Object component = getComponent.invoke(meta);

                if (component == null) return Optional.empty();

                Method getData = componentClass.getMethod("getCustomModelData");
                return Optional.of((Integer) getData.invoke(component));
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MMOBazaar] Failed to get CMD via component: " + e.getMessage());
                return Optional.empty();
            }

        } else {
            try {
                Method hasCmd = meta.getClass().getMethod("hasCustomModelData");
                boolean has = (boolean) hasCmd.invoke(meta);
                if (!has) return Optional.empty();

                Method getCmd = meta.getClass().getMethod("getCustomModelData");
                Integer data = (Integer) getCmd.invoke(meta);
                return Optional.ofNullable(data);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MMOBazaar] Failed to get legacy CMD: " + e.getMessage());
                return Optional.empty();
            }
        }
    }

    public static void setCustomModelData(ItemMeta meta, int modelId) {
        if (meta == null) return;

        if (currentVersion >= 21) {
            try {
                Class<?> componentClass = Class.forName("org.bukkit.inventory.meta.CustomModelDataComponent");
                Object component = componentClass.getConstructor().newInstance();

                Method setCmd = componentClass.getMethod("setCustomModelData", int.class);
                setCmd.invoke(component, modelId);

                Method setComponent = meta.getClass().getMethod("setCustomModelDataComponent", componentClass);
                setComponent.invoke(meta, component);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MMOBazaar] Failed to apply CustomModelDataComponent: " + e.getMessage());
            }
        } else {
            try {
                Method setCmd = meta.getClass().getMethod("setCustomModelData", Integer.class);
                setCmd.invoke(meta, modelId);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MMOBazaar] Failed to set legacy CustomModelData: " + e.getMessage());
            }
        }
    }
}
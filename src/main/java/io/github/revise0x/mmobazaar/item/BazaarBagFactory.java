package io.github.revise0x.mmobazaar.item;

import io.github.revise0x.mmobazaar.util.ItemMetaHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BazaarBagFactory {
    private final String displayName;
    private final List<String> lore;
    private final int customModelData;
    private final Material baseMaterial;

    public BazaarBagFactory() {
        // TODO Multi-language support in future
        this.displayName = "§6Bazaar Bag";
        this.lore = List.of("§7Right-click to open your shop.");
        this.customModelData = 7001;
        this.baseMaterial = Material.RABBIT_HIDE;
    }

    public ItemStack create() {
        ItemStack item = new ItemStack(baseMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            ItemMetaHelper.setCustomModelData(meta, customModelData);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isBazaarBag(ItemStack item) {
        if (item == null || item.getType() != baseMaterial) return false;
        ItemMeta meta = item.getItemMeta();
        return ItemMetaHelper.getCustomModelData(meta).map(data -> data == customModelData).orElse(false);
    }
}

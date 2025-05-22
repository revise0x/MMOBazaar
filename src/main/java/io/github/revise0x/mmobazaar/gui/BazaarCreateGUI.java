package io.github.revise0x.mmobazaar.gui;

import io.github.revise0x.mmobazaar.bazaar.BazaarManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

public class BazaarCreateGUI {
    private final BazaarManager manager;

    public BazaarCreateGUI(BazaarManager manager) {
        this.manager = manager;
    }

    public void open(Player player) {
        new AnvilGUI.Builder()
                .onClose((stateSnapshot) -> {
                    if (stateSnapshot.getText() == null || stateSnapshot.getText().trim().isEmpty()) {
                        player.sendMessage("§cBazaar name cannot be empty.");
                    }

                    // TODO Create bazaar, open new gui for it

                    player.sendMessage("§aBazaar created: §f" + stateSnapshot.getText());
                })
                .text("Enter bazaar name")
                .itemLeft(new org.bukkit.inventory.ItemStack(org.bukkit.Material.NAME_TAG))
                .title("Bazaar Name")
                .plugin(org.bukkit.Bukkit.getPluginManager().getPlugin("MMOBazaar"))
                .open(player);
    }
}
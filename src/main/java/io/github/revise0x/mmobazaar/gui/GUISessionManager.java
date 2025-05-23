package io.github.revise0x.mmobazaar.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class GUISessionManager {
    private final Map<UUID, BazaarOwnerGUI> ownerGuis = new HashMap<>();

    public void setOwnerGUI(UUID playerId, BazaarOwnerGUI gui) {
        ownerGuis.put(playerId, gui);
    }

    public void removeOwnerGUI(UUID playerId) {
        ownerGuis.remove(playerId);
    }

    public Optional<BazaarOwnerGUI> getOwnerGUI(UUID playerId) {
        return Optional.ofNullable(ownerGuis.get(playerId));
    }

    private final Map<UUID, BazaarCustomerGUI> customerGUIs = new HashMap<>();

    public void setCustomerGUI(UUID playerId, BazaarCustomerGUI gui) {
        customerGUIs.put(playerId, gui);
    }

    public Optional<BazaarCustomerGUI> getCustomerGUI(UUID playerId) {
        return Optional.ofNullable(customerGUIs.get(playerId));
    }

    public void removeCustomerGUI(UUID playerId) {
        customerGUIs.remove(playerId);
    }

    private final Map<UUID, ConfirmPurchaseGUI> confirming = new HashMap<>();

    public void setConfirming(UUID playerId, ConfirmPurchaseGUI gui) {
        confirming.put(playerId, gui);
    }

    public Optional<ConfirmPurchaseGUI> getConfirming(UUID playerId) {
        return Optional.ofNullable(confirming.get(playerId));
    }

    public void removeConfirming(UUID playerId) {
        confirming.remove(playerId);
    }
}

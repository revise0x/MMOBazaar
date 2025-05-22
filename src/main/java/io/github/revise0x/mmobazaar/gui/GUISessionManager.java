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
}

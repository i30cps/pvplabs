package dev.rotator.pvplabs.kit;

import java.util.*;

public class KitManager {

    private final InventoryIO inventoryIO; // handles file IO for kits
    private final Map<String, Kit> loadedKits = new HashMap<>();
    private final Map<UUID, Map<String, Kit>> playerKitCache = new HashMap<>();

    public KitManager(InventoryIO inventoryIO) {
        this.inventoryIO = inventoryIO;
    }

    /**
     * Returns a Kit by name. Loads it from file if not already loaded.
     */
    public Kit getKit(String name) {
        // check cache first
        if (loadedKits.containsKey(name)) {
            return loadedKits.get(name);
        }

        // load from InventoryIO
        Kit kit = inventoryIO.loadKit(name);
        if (kit != null) {
            loadedKits.put(name, kit);
        }
        return kit;
    }

    /**
     * Returns a Kit for a specific player by UUID.
     * Can be used to track per-player kit usage, cooldowns, etc.
     */
    public Kit getKit(String name, UUID playerId) {
        if (!inventoryIO.hasKit(name, playerId)) return getKit(name);
        // initialize player cache if needed
        playerKitCache.putIfAbsent(playerId, new HashMap<>());
        Map<String, Kit> playerKits = playerKitCache.get(playerId);

        return playerKits.computeIfAbsent(name, e -> inventoryIO.loadKit(name, playerId));
    }

    /**
     * Optional: clear cached kits for memory management.
     */
    public void clearCache() {
        loadedKits.clear();
        playerKitCache.clear();
    }
}

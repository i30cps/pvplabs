package dev.rotator.pvplabs.customitems;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CooldownManager {

    private static final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    private CooldownManager() {}

    public static boolean tryUse(UUID player, String itemId, long cooldownMillis) {
        if (cooldownMillis <= 0) return true;

        long now = System.currentTimeMillis();
        long expires = cooldowns
                .computeIfAbsent(player, k -> new HashMap<>())
                .getOrDefault(itemId, 0L);

        if (expires > now) return false;

        cooldowns.get(player).put(itemId, now + cooldownMillis);
        return true;
    }

    public static long getRemaining(UUID player, String itemId) {
        long now = System.currentTimeMillis();
        return Math.max(0, cooldowns
                .getOrDefault(player, Map.of())
                .getOrDefault(itemId, 0L) - now);
    }
}

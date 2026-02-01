package dev.rotator.util;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public final class PlayerUtils {

    private PlayerUtils() {}

    @SafeVarargs
    public static List<Player> combine(List<Player>... lists) {
        List<Player> result = new ArrayList<>();
        for (List<Player> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    public static void clearPotionEffects(Player p) {
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
    }

}

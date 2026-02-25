package dev.rotator.pvplabs.util;

import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

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

    public static void resetPlayer(Player p) {
        p.getInventory().clear();
        clearPotionEffects(p);
        p.setGameMode(GameMode.ADVENTURE);
        p.setFireTicks(0);
        p.setExp(0);
        p.setLevel(0);
        p.setFlying(false);
        p.setGlowing(false);
        p.setAllowFlight(false);
        p.setVelocity(new Vector(0,0,0));

        AttributeInstance attr = p.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) p.setHealth(attr.getValue());
    }
}

package dev.rotator.game.behaviors;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public interface DamageBehavior {
    /**
     * This method gets called whenever a player in the game gets damaged.
     * @param p The player who was damaged.
     * @param damageCause The cause of the damage.
     * @param finalDamage the actual damage that would be dealt
     * @return true if the damage should be allowed, false to cancel it.
     */
    boolean onPlayerDamage(Player p, EntityDamageEvent.DamageCause damageCause, double finalDamage);
}

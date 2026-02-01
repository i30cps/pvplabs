package dev.rotator.game.behaviors;

import org.bukkit.entity.Player;

public interface HitBehavior extends GameBehavior {
    /**
     * Runs every time a player in a game gets hit by another player.
     * @param p1 The player who did the hit.
     * @param p2 The player who was attacked.
     * @return True to allow this event, false to cancel it.
     */
    boolean onHit(Player p1, Player p2);
}

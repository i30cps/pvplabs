package dev.rotator.game.behaviors;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface MoveBehavior extends GameBehavior {
    /**
     * Runs every time a player in a game moves.
     * @return True to allow this event, false to cancel it.
     */
    boolean onMove(Player p, Location moveTo);
}

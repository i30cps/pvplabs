package dev.rotator.game.behaviors;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface QuitBehavior extends GameBehavior {
    /**
     * Runs every time a player in a game moves.
     */
    void onQuit(Player p);
}

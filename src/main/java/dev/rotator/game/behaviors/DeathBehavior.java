package dev.rotator.game.behaviors;

import org.bukkit.entity.Player;

public interface DeathBehavior {
    /**
     * Runs every time a player in the game takes is taking enough damage to cause death.
     * @param p The player.
     * @return True to allow the death, false to cancel it (you should probably handle it your own way though).
     */
    boolean onPlayerDeath(Player p);
}

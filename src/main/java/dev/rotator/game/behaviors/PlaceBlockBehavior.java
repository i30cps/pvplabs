package dev.rotator.game.behaviors;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface PlaceBlockBehavior extends GameBehavior {
    /**
     * To be run whenever a player in the game places a block.
     * @param p The player placing the block.
     * @param block The block that was placed.
     * @return true if the block placement should be allowed, false to cancel it.
     */
    boolean onPlace(Player p, Block block);
}

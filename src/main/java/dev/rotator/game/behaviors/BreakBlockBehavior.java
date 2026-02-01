package dev.rotator.game.behaviors;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface BreakBlockBehavior extends GameBehavior {
    /**
     * To be run whenever a player in the game breaks a block.
     * @param p The player breaking the block.
     * @param block The block that was broken.
     * @return true if the block break should be allowed, false to cancel it.
     */
    boolean onBreak(Player p, Block block);
}

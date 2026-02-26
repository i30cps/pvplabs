package dev.rotator.pvplabs.game.behaviors;

import com.sk89q.worldedit.math.BlockVector3;
import dev.rotator.pvplabs.game.GameBehavior;
import dev.rotator.pvplabs.game.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MapBreakingBehavior implements GameBehavior {
    private final GameMap map;
    private boolean allowBreaking;
    private final Location lowerCorner;

    public MapBreakingBehavior(GameMap map, Location lowerCorner) {
        this.map = map;
        this.lowerCorner = lowerCorner;
        allowBreaking = true;
    }

    public void setAllowBreaking(boolean allowBreaking) {
        this.allowBreaking = allowBreaking;
    }

    private boolean shouldBeAbleToBreak(Block block) {
        if (allowBreaking) return true;
        return !map.isInOriginalBlock(lowerCorner, BlockVector3.at(block.getX(), block.getY(), block.getZ()));
    }

    @Override
    public boolean onBreak(Player p, Block block) {
        boolean result = shouldBeAbleToBreak(block);
        Bukkit.getLogger().info("Block break at " + block.getX() + " " + block.getY() + " " + block.getZ() + ": " + result);
        return result;
    }
}

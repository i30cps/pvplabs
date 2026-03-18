package dev.rotator.pvplabs.game.behaviors;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;

import dev.rotator.pvplabs.game.GameBehavior;
import dev.rotator.pvplabs.game.GameMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MapCleanupBehavior implements GameBehavior {
    private static final double OUTSIDE_THRESHOLD = 5.0;

    private final GameMap map;
    private final Location lowerCorner;

    public MapCleanupBehavior(GameMap gameMap, Location lowerCorner) {
        this.map = gameMap;
        this.lowerCorner = lowerCorner;
    }

    @Override
    public void onEnd() {

        World bukkitWorld = lowerCorner.getWorld();
        if (bukkitWorld == null) return;

        // ----- Compute region corners -----
        Location upperCorner = lowerCorner.clone().add(
                map.getXSize() - 1,
                map.getYSize() - 1,
                map.getZSize() - 1
        );

        BlockVector3 min = BlockVector3.at(
                lowerCorner.getBlockX(),
                lowerCorner.getBlockY(),
                lowerCorner.getBlockZ()
        );

        BlockVector3 max = BlockVector3.at(
                upperCorner.getBlockX(),
                upperCorner.getBlockY(),
                upperCorner.getBlockZ()
        );

        // ----- Remove blocks using FAWE -----
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(bukkitWorld);
        CuboidRegion region = new CuboidRegion(weWorld, min, max);

        try (EditSession editSession = WorldEdit.getInstance()
                .newEditSessionBuilder()
                .world(weWorld)
                .fastMode(true)
                .build()) {

            editSession.setBlocks((Region) region, BlockTypes.AIR.getDefaultState());
        }

        // ----- Remove entities in area -----
        // Use nearbyEntities with diagonal radius for performance
        double dx = map.getXSize();
        double dy = map.getYSize();
        double dz = map.getZSize();

        double radius = Math.sqrt(dx * dx + dy * dy + dz * dz) / 2.0 + OUTSIDE_THRESHOLD*2;

        Location center = lowerCorner.clone().add(dx / 2.0, dy / 2.0, dz / 2.0);

        for (Entity entity : bukkitWorld.getNearbyEntities(center, radius, radius, radius)) {

            if (entity instanceof Player) continue;

            Location loc = entity.getLocation();

            if (isInside(loc, lowerCorner, upperCorner, OUTSIDE_THRESHOLD)) {
                entity.remove();
            }
        }
    }

    private boolean isInside(Location loc, Location min, Location max, double threshold) {
        return loc.getBlockX() >= min.getBlockX()-threshold &&
                loc.getBlockX() <= max.getBlockX()+threshold &&
                loc.getBlockY() >= min.getBlockY()-threshold &&
                loc.getBlockY() <= max.getBlockY()+threshold &&
                loc.getBlockZ() >= min.getBlockZ()-threshold &&
                loc.getBlockZ() <= max.getBlockZ()+threshold;
    }
}
package dev.rotator.pvplabs.game;


import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import dev.rotator.pvplabs.PvPLabs;
import dev.rotator.pvplabs.util.SchematicUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GameMap {
    protected final String name;              // Map name
    protected final String schematicFile;     // Path to the .schem file
    protected final int xSize, zSize;         // Dimensions
    // Sparse set of original block positions (relative to lower corner)
    private final Set<BlockVector3> originalBlocks = new HashSet<>();

    public GameMap(String name, String schematicFile) {
        this.name = name;
        this.schematicFile = schematicFile;

        File file = new File(PvPLabs.getMain().getDataFolder(), schematicFile);
        Clipboard clipboard = null;
        try (ClipboardReader reader = ClipboardFormats.findByFile(file)
                .getReader(new FileInputStream(file))) {

            clipboard = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Region region = clipboard.getRegion();
        // Iterate over the clipboard to add all original blocks to the sparse set
        Clipboard finalClipboard = clipboard;
        region.forEach(e -> {
            if (finalClipboard.getBlock(e).getBlockType() == BlockTypes.AIR) return;
            // If this is air, don't do the following:
            BlockVector3 v = BlockVector3.at(e.x(), e.y(), e.z()); // canonical object
            originalBlocks.add(v);
        });

        this.xSize = region.getWidth();
        this.zSize = region.getLength();
    }

    public boolean isInOriginalBlock(Location lowerCorner, BlockVector3 pos) {
        BlockVector3 relative = BlockVector3.at(
                pos.x() - lowerCorner.getBlockX(),
                pos.y() - lowerCorner.getBlockY(),
                pos.z() - lowerCorner.getBlockZ()
        );

        return originalBlocks.contains(relative);
    }

    // Getters
    public String getName() { return name; }
    public int getXSize() { return xSize; }
    public int getZSize() { return zSize; }

    public void pasteSchematic(Location location) {
        SchematicUtils.paste(schematicFile, location);
    }

    /**
     * Is the object inside the bounds of the map?
     * @param mapLocation The location of the map (lower corner).
     * @param objectLocation The location of the object.
     * @return Whether the object is inside the map
     */
    public boolean isInside(Location mapLocation, Location objectLocation) {
        double minX = mapLocation.getX();
        double minZ = mapLocation.getZ();

        double maxX = minX + xSize;
        double maxZ = minZ + zSize;

        double objX = objectLocation.getX();
        double objZ = objectLocation.getZ();

        return objX >= minX && objX <= maxX
                && objZ >= minZ && objZ <= maxZ;
    }
}

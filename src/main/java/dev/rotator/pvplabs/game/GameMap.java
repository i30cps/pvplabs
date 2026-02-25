package dev.rotator.pvplabs.game;


import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.regions.Region;
import dev.rotator.pvplabs.PvPLabs;
import dev.rotator.pvplabs.util.SchematicUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GameMap {
    protected final String name;              // Map name
    protected final String schematicFile;     // Path to the .schem file
    protected final int xSize, zSize;         // Dimensions

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

        this.xSize = region.getWidth();
        this.zSize = region.getLength();
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

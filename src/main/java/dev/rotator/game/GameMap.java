package dev.rotator.game;


import dev.rotator.util.SchematicUtils;
import org.bukkit.Location;

public class GameMap {
    protected final String name;              // Map name
    protected final String schematicFile;     // Path to the .schem file
    protected final int xSize, zSize;         // Dimensions

    public GameMap(String name, String schematicFile,
                  int xSize, int zSize) {
        this.name = name;
        this.schematicFile = schematicFile;
        this.xSize = xSize;
        this.zSize = zSize;
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

package dev.rotator.game.ctf;


import dev.rotator.game.GameMap;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class CTFMap extends GameMap {
    private final Vector redSpawn;          // Relative spawn points for red team
    private final Vector blueSpawn;         // Relative spawn points for blue team
    private final Vector redFlagSpawn;      // Relative location of red flag
    private final Vector blueFlagSpawn;     // Relative location of blue flag

    public CTFMap(String name, String schematicFile,
                  int xSize, int zSize,
                  Vector redSpawn, Vector blueSpawn,
                  Vector redFlagSpawn, Vector blueFlagSpawn) {
        super(name, schematicFile, xSize, zSize);
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.redFlagSpawn = redFlagSpawn;
        this.blueFlagSpawn = blueFlagSpawn;
    }

    // Getters
    public Vector getRedSpawn() { return redSpawn; }
    public Vector getBlueSpawn() { return blueSpawn; }
    public Vector getRedFlagSpawn() { return redFlagSpawn; }
    public Vector getBlueFlagSpawn() { return blueFlagSpawn; }
}

package dev.rotator.pvplabs.game.kitduels;

import dev.rotator.pvplabs.game.GameMap;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class KitDuelMap extends GameMap {
    private final Vector spawn1, spawn2;          // Relative spawn points for team 1

    public KitDuelMap(String name, String schematicFile, Vector spawn1, Vector spawn2) {
        super(name, schematicFile);
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
    }

    public Vector getSpawn1() {
        return spawn1;
    }

    public Vector getSpawn2() {
        return spawn2;
    }

    public Location getSpawn1(Location location) {
        return location.clone().add(spawn1);
    }

    public Location getSpawn2(Location location) {
        return location.clone().add(spawn2);
    }
}

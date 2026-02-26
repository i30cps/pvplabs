package dev.rotator.pvplabs.game.kitduels;

import dev.rotator.pvplabs.game.GameMap;
import dev.rotator.pvplabs.util.Pair;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class KitDuelMap extends GameMap {
    private final Vector spawn1, spawn2;          // Relative spawn points for team 1
    private final Pair<Float, Float> rotation1, rotation2;

    private float vectorToYaw(Vector vec) {
        return (float) Math.toDegrees(Math.atan2(-vec.getX(), vec.getZ()));
    }

    public KitDuelMap(String name, String schematicFile, Vector spawn1, Vector spawn2) {
        super(name, schematicFile);
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        // Rotation1 will be the angle where something in position spawn1 will face spawn2
        // Rotation2 will be the angle where something in position spawn2 will face spawn1

        // Direction from spawn1 → spawn2
        Vector dir1 = spawn2.clone().subtract(spawn1);
        float yaw1 = vectorToYaw(dir1);
        float pitch1 = 0f;

        // Direction from spawn2 → spawn1
        Vector dir2 = spawn1.clone().subtract(spawn2);
        float yaw2 = vectorToYaw(dir2);
        float pitch2 = 0f;

        this.rotation1 = new Pair<>(yaw1, pitch1);
        this.rotation2 = new Pair<>(yaw2, pitch2);
    }
    public KitDuelMap(String name, String schematicFile, Vector spawn1, Vector spawn2,
                      Pair<Float, Float> rotation1,  Pair<Float, Float> rotation2) {
        super(name, schematicFile);
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.rotation1 = rotation1;
        this.rotation2 = rotation2;
    }

    public Vector getSpawn1() {
        return spawn1;
    }

    public Vector getSpawn2() {
        return spawn2;
    }

    public Pair<Float, Float> getRotation1() {
        return rotation1;
    }

    public Pair<Float, Float> getRotation2() {
        return rotation2;
    }

    public Location getSpawn1(Location location) {
        return location.clone().add(spawn1);
    }

    public Location getSpawn2(Location location) {
        return location.clone().add(spawn2);
    }
}

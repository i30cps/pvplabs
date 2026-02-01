package dev.rotator.util;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import dev.rotator.PvPLabs;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;

public final class SchematicUtils {

    private SchematicUtils() {}

    public static void paste(String schematicFilePath, Location location) {
        File file = new File(PvPLabs.getMain().getDataFolder(), schematicFilePath);

        if (!file.exists()) {
            Bukkit.getLogger().severe("Schematic " + schematicFilePath + " does not exist.");
            return;
        }

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        ClipboardReader reader = null;
        World world = FaweAPI.getWorld(location.getWorld().getName());

        if (format == null) {
            Bukkit.getLogger().severe("Schematic " + schematicFilePath + " has a null schematic format.");
            return;
        }

        try {
            reader = format.getReader(Files.newInputStream(file.toPath()));
        } catch (Exception e) {
            Bukkit.getLogger().severe(e.getClass().getName() + ": Schematic "
                    + schematicFilePath + " failed pasting; reader failed to get: "
                    + e.getMessage());
            return;
        }

        if (reader == null) {
            Bukkit.getLogger().severe("Schematic " + schematicFilePath + " failed pasting; reader is null.");
            return;
        }

        Clipboard clipboard = null;

        try {
            clipboard = reader.read();
        } catch (Exception e) {
            Bukkit.getLogger().severe(e.getClass().getName() + ": Schematic "
                    + schematicFilePath + " failed pasting; clipboard failed to read: "
                    + e.getMessage());
            return;
        }

        if (clipboard == null) {
            Bukkit.getLogger().severe("Schematic " + schematicFilePath + " failed pasting; clipboard is null.");
            return;
        }

        BlockVector3 to = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build()) {
            try (ClipboardHolder holder = new ClipboardHolder(clipboard)) {
                Operation operation = holder.createPaste(editSession).to(to).ignoreAirBlocks(false).build();
                Operations.complete(operation);
            }
        }
    }
}

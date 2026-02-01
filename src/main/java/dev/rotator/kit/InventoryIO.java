package dev.rotator.kit;

import dev.rotator.PvPLabs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public final class InventoryIO {
    private final File kitsFolder;
    private final File playerKitsFolder;

    public InventoryIO(File kitsFolder) {
        this.kitsFolder = kitsFolder;
        if (!kitsFolder.exists()) {
            kitsFolder.mkdirs();
        }
        playerKitsFolder = new File(kitsFolder, "playerkits");
        if (!playerKitsFolder.exists())
            playerKitsFolder.mkdirs();
    }

    public static void savePlayerInventory(Player p, File file) throws IOException {
        YamlConfiguration config = new YamlConfiguration();

        config.set("inventory", p.getInventory().getContents());
        config.set("armor", p.getInventory().getArmorContents());
        config.set("offhand", p.getInventory().getItemInOffHand());
        List<Map<String, Object>> effects = new ArrayList<>();
        for (PotionEffect effect : p.getActivePotionEffects()) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", effect.getType().getKey().toString());
            map.put("duration", effect.getDuration());
            map.put("amplifier", effect.getAmplifier());
            map.put("ambient", effect.isAmbient());
            map.put("particles", effect.hasParticles());
            map.put("icon", effect.hasIcon());
            effects.add(map);
        }

        config.set("effects", effects);
        config.save(file);
    }

    /**
     * Loads a default kit from a name.
     * @param name The name (ID) of the kit.
     * @return The kit.
     */
    public Kit loadKit(String name) {
        File file = new File(kitsFolder, name + ".yml");
        if (!file.exists()) return null;

        return loadKit(file);
    }

    /**
     * Load a player kit from a name and the player UUID.
     * @param name The name (ID) of the kit.
     * @param playerUUID The player UUID.
     * @return The kit.
     */
    public Kit loadKit(String name, UUID playerUUID) {
        // TODO: Stop checking for kit existence twice somehow
        File playerFolder = new File(playerKitsFolder, playerUUID.toString());

        if (!playerFolder.exists() || !playerFolder.isDirectory())
            return null;

        File kitFile = new File(playerFolder, name + ".yml");
        if (!kitFile.exists() || !kitFile.isFile())
            return null;

        return loadKit(kitFile);
    }

    /**
     * Load a kit from a file.
     * @param file The file.
     * @return The kit.
     */
    private Kit loadKit(@NotNull File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // load inventory items
        ItemStack[] inventory = config.getList("inventory").toArray(new ItemStack[0]);
        ItemStack[] armor = config.getList("armor").toArray(new ItemStack[0]);
        ItemStack offhand = config.getObject("offhand", ItemStack.class);
        List<Map<String, Object>> effects = (List<Map<String, Object>>) config.getList("effects");

        return new Kit(inventory, armor, offhand, effects);
    }

    public boolean hasKit(String name, UUID playerUUID) {
        File playerFolder = new File(playerKitsFolder, playerUUID.toString());
        if (!playerFolder.exists() || !playerFolder.isDirectory()) {
            return false;
        }

        File kitFile = new File(playerFolder, name + ".yml");
        return kitFile.exists() && kitFile.isFile();
    }
}

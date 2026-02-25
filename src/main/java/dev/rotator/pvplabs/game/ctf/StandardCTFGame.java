package dev.rotator.pvplabs.game.ctf;

import dev.rotator.pvplabs.game.Game;
import dev.rotator.pvplabs.game.GamePlayer;
import dev.rotator.pvplabs.util.PlayerUtils;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * This class is deprecated because it was using outdated features that have been greatly changed, and
 * it is not my current focus at this moment.
 */
@Deprecated
public class StandardCTFGame extends Game {
    private final ArrayList<Player> redTeam, blueTeam;
    private final CTFMap map;

    private final Location redSpawn, blueSpawn, redFlagSpawn, blueFlagSpawn;

    public boolean onHit(Player p1, Player p2) {
        Color c1 = getColor(p1), c2 = getColor(p2);
        if (c1 == null) return false;
        if (c2 == null) return false;

        return c1.equals(c2);
    }

    public Color getColor(Player p) { // TODO: Replace with better implementation
        if (redTeam.contains(p)) return Color.RED;
        if (blueTeam.contains(p)) return Color.BLUE;
        return null;
    }

    /**
     * Spawns the player.
     * @param p The player.
     */
    private void spawnPlayer(Player p) { // TODO: Custom inventory layouts
        Color color = getColor(p);
        if (color == null) throw new NoSuchElementException("This player is not in this game!");

        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }

        p.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).armorColor(color).build());
        p.getInventory().setLeggings(new ItemBuilder(Material.CHAINMAIL_LEGGINGS).build());
        p.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).armorColor(color).build());
        p.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).armorColor(color).build());
        p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
        p.getInventory().setItem(1, new ItemStack(Material.SHEARS));
        p.getInventory().setItem(2, new ItemStack(Material.GOLDEN_APPLE));


        ItemStack potion = new ItemStack(Material.POTION);

        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        if (meta != null) {
            // Add the Instant Damage effect with amplifier 9 (level 10, amplifier is zero-based)
            meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 20, 9), true);

            potion.setItemMeta(meta);
        }

        p.getInventory().setItem(8, potion);

        p.setHealth(20);
        p.setSaturation(0);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.SURVIVAL);

        Location spawn;
        ItemStack wool;

        if (color.equals(Color.RED)) {
            spawn = redSpawn;
            wool = new ItemStack(Material.RED_WOOL, 8);
        } else {
            spawn = blueSpawn;
            wool = new ItemStack(Material.BLUE_WOOL, 8);
        }

        p.getInventory().setItem(3, wool);
        p.teleport(spawn);
    }

    public StandardCTFGame(ArrayList<Player> team1, ArrayList<Player> team2, Location lowerCorner, CTFMap ctfMap) {
        super(lowerCorner, ctfMap.getXSize(), ctfMap.getZSize(), PlayerUtils.combine(team1, team2));

        if (new Random().nextBoolean()) {
            redTeam = team1;
            blueTeam = team2;
        } else {
            blueTeam = team1;
            redTeam = team2;
        }

        players.addAll(redTeam);
        players.addAll(blueTeam);

        this.map = ctfMap;

        redSpawn = lowerCorner.clone().add(map.getRedSpawn());
        blueSpawn = lowerCorner.clone().add(map.getBlueSpawn());
        redFlagSpawn = lowerCorner.clone().add(map.getRedFlagSpawn());
        blueFlagSpawn = lowerCorner.clone().add(map.getBlueFlagSpawn());
    }

    public void start() {
        map.pasteSchematic(lowerCorner);

        for (Player p : players) {
            spawnPlayer(p);
            GamePlayer.getPlayer(p).setFallDamage(false);
        }

        // TODO: Finish this! (add the part about the flag)
    }

    public boolean onPlayerDeath(Player p) {
        spawnPlayer(p);
        return false;
    }
}

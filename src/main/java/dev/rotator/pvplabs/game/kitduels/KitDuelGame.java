package dev.rotator.pvplabs.game.kitduels;

import dev.rotator.pvplabs.PvPLabs;
import dev.rotator.pvplabs.game.Game;
import dev.rotator.pvplabs.game.behaviors.*;
import dev.rotator.pvplabs.kit.Kit;
import dev.rotator.pvplabs.util.PlayerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
@Deprecated
/**
 * This class is currently deprecated because it needs lots of work (it uses outdated features that no longer exist.)
 */
public class KitDuelGame extends Game {
    private final ArrayList<Player> team1, team2;
    private final Kit kit1, kit2;
    private final KitDuelMap map;
    private final Map<UUID, BukkitTask> outOfBoundsTasks = new HashMap<>();
    private final KitDuelSettings settings = new KitDuelSettings();

    public KitDuelGame(Location lowerCorner, ArrayList<Player> team1, ArrayList<Player> team2, Kit kit1, Kit kit2, KitDuelMap map) {
        super(lowerCorner, map.getXSize(), map.getZSize(), PlayerUtils.combine(team1, team2));

        this.team1 = team1;
        this.team2 = team2;
        this.kit1 = kit1;
        this.kit2 = kit2;
        this.map = map;
    }

    public KitDuelSettings getSettings() {
        return settings;
    }

    
    public boolean onHit(Player p1, Player p2) {
        if (settings.friendlyFire()) return true;
        if (team1.contains(p1) && team1.contains(p2)) return false;
        return !team2.contains(p1) || !team2.contains(p2);
    }

    
    public void start() {
        map.pasteSchematic(lowerCorner);

        for (Player p : team1) {
            p.teleport(map.getSpawn1(lowerCorner));
            kit1.apply(p);
        }

        for (Player p : team2) {
            p.teleport(map.getSpawn2(lowerCorner));
            kit2.apply(p);
        }

        for (Player p : players) {
            p.setFoodLevel(20);
            p.setSaturation(20);
            p.setGameMode(GameMode.SURVIVAL);
            p.setFireTicks(0);
            p.setHealth(Objects.requireNonNull(p.getAttribute(Attribute.MAX_HEALTH)).getValue());
            p.sendMessage("§7Game started!");
        }
    }

    private void playerDies(Player p) {
        team1.remove(p);
        team2.remove(p);

        p.setGameMode(GameMode.SPECTATOR);
        p.getInventory().clear();
        p.getActivePotionEffects().clear();
        p.sendTitle(ChatColor.RED + "You died", "", 5, 20, 5);

        ArrayList<Player> winners = null, losers = null;

        if (team1.isEmpty()) {
            winners = team2;
            losers = team1;
        }
        if (team2.isEmpty()) {
            winners = team1;
            losers = team2;
        }

        if (winners == null) return;

        for (Player winner : winners) {
            winner.sendTitle(ChatColor.GOLD + "You win!", "", 5, 40, 5);
        }
        for (Player loser : losers) {
            loser.sendTitle(ChatColor.RED + "You lost!", "", 5, 40, 5);
        }

        new BukkitRunnable() {
            
            public void run() {
                endGame();
            }
        }.runTaskLater(PvPLabs.getMain(), 60L);
    }

    
    public void onQuit(Player p) {
        playerDies(p);
    }

    
    public boolean onPlayerDeath(Player p) {
        playerDies(p);
        return false;
    }

    
    public boolean onMove(Player p, Location moveTo) {
        if (map.isInside(lowerCorner, moveTo)) {
            BukkitTask task = outOfBoundsTasks.remove(p.getUniqueId());
            if (task != null) {
                task.cancel();
            }
            return true;
        }

        if (p.getGameMode().equals(GameMode.SPECTATOR)) return false;

        if (outOfBoundsTasks.containsKey(p.getUniqueId())) return true;

        p.sendMessage(ChatColor.RED + "Return to the arena within 10 seconds!");
        p.sendTitle(ChatColor.RED + "Return to the arena!", ChatColor.YELLOW + "Otherwise you will be eliminated in 10 seconds!", 5, 30, 5);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(PvPLabs.getMain(), () -> {
            // Double-check they're still out
            if (!map.isInside(lowerCorner, p.getLocation())) {
                playerDies(p);
            }
            outOfBoundsTasks.remove(p.getUniqueId());
        }, 20L * 10);

        outOfBoundsTasks.put(p.getUniqueId(), task);

        return true;
    }
}

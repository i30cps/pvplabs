package dev.rotator.pvplabs.ranked;

import dev.rotator.pvplabs.PvPLabs;
import dev.rotator.pvplabs.game.Game;
import dev.rotator.pvplabs.game.behaviors.*;
import dev.rotator.pvplabs.game.kitduels.KitDuelMap;
import dev.rotator.pvplabs.game.kitduels.KitDuelSettings;
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

public class MatchDuelGame extends Game {
    private final Player p1, p2;
    private final Kit kit;
    private final KitDuelMap map;
    private final KitDuelSettings settings = new KitDuelSettings();
    private final MatchResultHandler resultHandler;

    public MatchDuelGame(Location lowerCorner, Player p1, Player p2, Kit kit,
                         KitDuelMap map, MatchResultHandler matchResultHandler) {
        super(lowerCorner, map.getXSize(), map.getZSize(), List.of(p1, p2));
        this.p1 = p1;
        this.p2 = p2;
        this.kit = kit;
        this.map = map;
        this.resultHandler = matchResultHandler;

        addBehavior(new DieOrQuitBehavior(this::playerDies));
        addBehavior(new OutOfBoundsBehavior(map, lowerCorner, 10, this::playerDies));
    }

    public KitDuelSettings getSettings() {
        return settings;
    }

    @Override
    public void start() {
        map.pasteSchematic(lowerCorner);

        PlayerUtils.resetPlayer(p1);
        PlayerUtils.resetPlayer(p2);

        p1.setGameMode(GameMode.SURVIVAL);
        p2.setGameMode(GameMode.SURVIVAL);

        p1.teleport(map.getSpawn1(lowerCorner));
        kit.apply(p1);

        p2.teleport(map.getSpawn2(lowerCorner));
        kit.apply(p2);
    }

    private void playerDies(Player p) {
        p.setGameMode(GameMode.SPECTATOR);
        p.getInventory().clear();
        p.getActivePotionEffects().clear();

        Player winner, loser;

        if (p == p1) {
            winner = p2;
            loser = p1;
        } else {
            winner = p1;
            loser = p2;
        }

        winner.sendTitle(ChatColor.GREEN + "You win!", "", 5, 40, 5);
        loser.sendTitle(ChatColor.RED + "You lost!", "", 5, 40, 5);

        new BukkitRunnable() {
            @Override
            public void run() {
                endGame();
                resultHandler.onFinish(winner, loser);
            }
        }.runTaskLater(PvPLabs.getMain(), 60L);
    }
}

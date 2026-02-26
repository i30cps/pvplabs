package dev.rotator.pvplabs.ranked;

import dev.rotator.pvplabs.PvPLabs;
import dev.rotator.pvplabs.game.Game;
import dev.rotator.pvplabs.game.PvPLabsPlayer;
import dev.rotator.pvplabs.game.behaviors.*;
import dev.rotator.pvplabs.game.kitduels.KitDuelMap;
import dev.rotator.pvplabs.kit.Kit;
import dev.rotator.pvplabs.util.PlayerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class MatchDuelGame extends Game {
    private final Player p1, p2;
    private final PvPLabsPlayer gp1, gp2;
    private final RankedPlayerData p1Data, p2Data;
    private final Kit kit;
    private final KitDuelMap map;
    private final MatchResultHandler resultHandler;
    private BukkitTask sidebarUpdater;
    private final MapBreakingBehavior breakingBehavior;
    private int secondsElapsed;
    private final MatchSidebar sidebar;

    public MatchDuelGame(Location lowerCorner, Player p1, Player p2,
                         RankedPlayerData p1Data, RankedPlayerData p2data, Kit kit,
                         KitDuelMap map, MatchResultHandler matchResultHandler) {
        super(lowerCorner, map.getXSize(), map.getZSize(), List.of(p1, p2));
        this.p1 = p1;
        this.p2 = p2;
        this.gp1 = PvPLabsPlayer.getPlayer(p1);
        this.gp2 = PvPLabsPlayer.getPlayer(p2);
        this.p1Data = p1Data;
        this.p2Data = p2data;
        this.kit = kit;
        this.map = map;
        this.resultHandler = matchResultHandler;
        this.sidebar = new MatchSidebar(this);

        addBehavior(new DieOrQuitBehavior(this::playerDies));
        addBehavior(new OutOfBoundsBehavior(map, lowerCorner, 10, this::playerDies));
        addBehavior(new OnEndBehavior(() -> {
            // This will always happen after the start is called, so there will never be a null pointer exception.
            sidebarUpdater.cancel();
            gp1.defaultSidebarContents();
            gp2.defaultSidebarContents();
        }));
        breakingBehavior = new MapBreakingBehavior(map, lowerCorner);
        addBehavior(breakingBehavior);
    }

    @Override
    public void start() {
        map.pasteSchematic(lowerCorner);

        PlayerUtils.resetPlayer(p1);
        PlayerUtils.resetPlayer(p2);

        p1.setGameMode(GameMode.SURVIVAL);
        p2.setGameMode(GameMode.SURVIVAL);

        p1.teleport(map.getSpawn1(lowerCorner));
        p1.setRotation(map.getRotation1().getFirst(),  map.getRotation1().getSecond());
        kit.apply(p1);

        p2.teleport(map.getSpawn2(lowerCorner));
        p2.setRotation(map.getRotation2().getFirst(),  map.getRotation2().getSecond());
        kit.apply(p2);

        sidebarUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                secondsElapsed++;
                sidebar.update(secondsElapsed);
            }
        }.runTaskTimer(PvPLabs.getMain(), 0L, 20L);
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

    public void allowMapBreaking(boolean b) {
        breakingBehavior.setAllowBreaking(b);
    }

    // getters for p1, p2, gp1, gp2, p1Rating (p1Data#getDisplayedRating), p2Rating (p2Data#getDisplayedRating)
    public Player getP1() {
        return p1;
    }
    public Player getP2() {
        return p2;
    }

    public PvPLabsPlayer getGp1() {
        return gp1;
    }
    public PvPLabsPlayer getGp2() {
        return gp2;
    }
    public int getP1DisplayedRating() {
        return p1Data.getDisplayedRating();
    }
    public int getP2DisplayedRating() {
        return p2Data.getDisplayedRating();
    }

    public String getModeName() {
        return kit.getName();
    }
}

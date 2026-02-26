package dev.rotator.pvplabs.game;

import dev.rotator.pvplabs.PvPLabs;
import dev.rotator.pvplabs.game.ctf.CTFMap;
import dev.rotator.pvplabs.game.ctf.StandardCTFGame;
import dev.rotator.pvplabs.game.kitduels.KitDuelGame;
import dev.rotator.pvplabs.game.kitduels.KitDuelMap;
import dev.rotator.pvplabs.kit.Kit;
import dev.rotator.pvplabs.ranked.MatchDuelGame;
import dev.rotator.pvplabs.ranked.RankedPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GameManager {
    private final Map<PvPLabsPlayer, Game> gpToGame;
    private final Set<Game> ongoingGames;

    @Deprecated(forRemoval = true)
    private final KitDuelMap moleMap;
    @Deprecated(forRemoval = true)
    private final KitDuelMap flatMap;

    public GameManager() {
         gpToGame = new HashMap<>();
         ongoingGames = new HashSet<>();
         moleMap = new KitDuelMap(
                 "Mole", "maps/MoleMap.schem", new org.bukkit.util.Vector(16, 10, 5), new Vector(16, 10, 27)
         );
         flatMap = new KitDuelMap(
                 "Flat", "maps/Flat.schem", new org.bukkit.util.Vector(16, 10, 5), new Vector(16, 10, 27)
         );
    }

    protected void markInGame(Collection<Player> players, @NotNull Game game) {
        for (Player p : players)
            markInGame(p, game);
    }

    protected void markInGame(Player p, @NotNull Game game) {
        markInGame(PvPLabsPlayer.getPlayer(p), game);
    }

    protected void markInGame(PvPLabsPlayer gp, @NotNull Game game) {
        gpToGame.put(gp, game);
        gp.markInLobby(false);
    }

    protected void markNotInGame(Player p) {
        markNotInGame(PvPLabsPlayer.getPlayer(p));
    }

    protected void markNotInGame(PvPLabsPlayer gp) {
        gpToGame.remove(gp);
        gp.markInLobby(true);
    }

    protected void markNotInGame(Collection<Player> players) {
        for (Player p : players)
            markNotInGame(p);
    }

    public Game getGame(Player p) {
        return getGame(PvPLabsPlayer.getPlayer(p));
    }

    public Game getGame(PvPLabsPlayer gp) {
        return gpToGame.get(gp);
    }

    protected void markOnline(PvPLabsPlayer gp, boolean online) {
        gp.markOnline(online);
    }

    // Assume world to always be Bukkit.getWorlds().getFirst()
    // Test if a game can be placed at (10k, 10k). If so, return the location (10k, 100, 10k).
    // Otherwise, go through each corner of each game,
    // and test that corner with all other games to see if it's possible
    // to place a game in that corner.
    // I am fine with the O(N^2) approach because
    // once N (the number of ongoing games) surpasses 20, we're going to have different issues
    // occur before time complexity becomes an issue.

    // Use private final ArrayList<Game> ongoingGames to help.
    private Location getNextCorner(int xSize, int zSize) {
        World world = Bukkit.getWorlds().getFirst();

        // Start at a large coordinate for testing
        int startX = 10000;
        int startY = 100; // you used 100 in your example
        int startZ = 10000;
        int spacing = 50;

        // If no games yet, just return the starting point
        if (ongoingGames.isEmpty()) {
            return new Location(world, startX, startY, startZ);
        }

        // Try the initial test location
        Location testLoc = new Location(world, startX, startY, startZ);
        if (canPlaceAt(testLoc, xSize, zSize)) {
            return testLoc;
        }

        // Otherwise, check every corner of each existing game
        for (Game existing : ongoingGames) {
            Location lower = existing.getLowerCorner();
            int exX = lower.getBlockX();
            int exZ = lower.getBlockZ();
            int exXSize = existing.getXSize();
            int exZSize = existing.getZSize(); // assuming this is the Z-axis size

            // Four corners around the existing game
            int[][] candidates = new int[][]{
                    {exX + exXSize, startY, exZ + exZSize}, // top-right corner
                    {exX + exXSize, startY, exZ - zSize},   // bottom-right
                    {exX - xSize, startY, exZ + exZSize},  // top-left
                    {exX - xSize, startY, exZ - zSize}      // bottom-left
            };

            for (int[] coords : candidates) {
                Location loc = new Location(world, coords[0], coords[1], coords[2]);
                if (canPlaceAt(loc, xSize, zSize)) {
                    return loc;
                }
            }
        }

        // If no suitable corner found, fallback to an offset (naive)
        int offset = 0;
        while (true) {
            Location loc = new Location(world, startX + offset, startY, startZ + offset);
            if (canPlaceAt(loc, xSize, zSize)) return loc;
            offset += spacing;
        }
    }

    // Helper method to test if a game can be placed at the given location
    private boolean canPlaceAt(Location testLoc, int xSize, int zSize) {
        for (Game existing : ongoingGames) {
            Location lower = existing.getLowerCorner();
            int exX = lower.getBlockX();
            int exZ = lower.getBlockZ();
            int exXSize = existing.getXSize();
            int exZSize = existing.getZSize();

            boolean overlapX = testLoc.getBlockX() < exX + exXSize && testLoc.getBlockX() + xSize > exX;
            boolean overlapZ = testLoc.getBlockZ() < exZ + exZSize && testLoc.getBlockZ() + zSize > exZ;

            if (overlapX && overlapZ) return false;
        }
        return true;
    }


    public void startCTFGame(ArrayList<Player> team1, ArrayList<Player> team2, CTFMap map) {
        Game game = new StandardCTFGame(
                team1,
                team2,
                getNextCorner(map.getXSize(), map.getZSize()),
                map);
        markInGame(team1, game);
        markInGame(team2, game);
        startGame(game);
    }

    private KitDuelGame makeKitDuelGame(ArrayList<Player> team1, ArrayList<Player> team2, Kit kit1, Kit kit2, KitDuelMap map) {
        KitDuelGame game = new KitDuelGame(
                getNextCorner(map.getXSize(), map.getZSize()),
                team1,
                team2,
                kit1,
                kit2,
                map);
        markInGame(team1, game);
        markInGame(team2, game);
        return game;
    }

    private void startGame(Game game) {
        game.start();
        ongoingGames.add(game);
    }

    @Deprecated
    public void startMoleGame(Player p1, Player p2) {
        Kit moleKit = PvPLabs.getMain().getKitManager().getKit("mole");

        MatchDuelGame game = new MatchDuelGame(getNextCorner(moleMap.getXSize(), moleMap.getZSize()), p1, p2,
                new RankedPlayerData(p1.getUniqueId()), new RankedPlayerData(p2.getUniqueId()), moleKit, moleMap,
                ((winner, loser) -> {
            Bukkit.getLogger().info("Game finished (winner: " + winner.getName() + ", loser: " + loser.getName() + ")");
        }));

        startGame(game);
    }

    public void removeGame(Game game) {
        ongoingGames.remove(game);

    }

    public void startSwordGame(Player p, Player p2) {
        Kit swordKit = PvPLabs.getMain().getKitManager().getKit("sword");

        MatchDuelGame game = new MatchDuelGame(getNextCorner(flatMap.getXSize(), flatMap.getZSize()), p, p2,
                new RankedPlayerData(p.getUniqueId()), new RankedPlayerData(p2.getUniqueId()), swordKit, flatMap,
                ((winner, loser) -> {
                    Bukkit.getLogger().info("Sword game finished (winner: " + winner.getName() + ", loser: " + loser.getName() + ")");
                }));

        game.allowMapBreaking(false);
        startGame(game);
    }
}

package dev.rotator.pvplabs.game;

import dev.rotator.pvplabs.PvPLabs;
import dev.rotator.pvplabs.util.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Game {
    protected final Location lowerCorner;
    protected final int xSize, zSize;
    protected final List<Player> players;

    protected final List<GameBehavior> behaviors = new ArrayList<>();

    protected Game(Location lowerCorner, int xSize, int zSize, List<Player> players) {
        this.lowerCorner = lowerCorner;
        this.xSize = xSize;
        this.zSize = zSize;
        this.players = players;
        PvPLabs.getMain().getGameManager().markInGame(players, this);
    }

    public abstract void start();

    public Location getLowerCorner() { return lowerCorner; }
    public int getXSize() { return xSize; }
    public int getZSize() { return zSize; }
    public List<Player> getPlayers() { return List.copyOf(players); }

    protected void endGame() {
        handleEnd();

        for (Player p : players) {
            PlayerUtils.resetPlayer(p);
            p.teleport(PvPLabs.getMain().getSpawnLocation());
        }

        PvPLabs.getMain().getGameManager().markNotInGame(players);
        PvPLabs.getMain().getGameManager().removeGame(this);
    }

    public void addBehavior(GameBehavior behavior) {
        behaviors.add(behavior);
        behaviors.sort(Comparator.comparingInt(GameBehavior::getPriority));
    }

    public boolean handleBreak(Player p, Block block) {
        for (GameBehavior b : behaviors) {
            if (!b.onBreak(p, block)) {
                return false;
            }
        }
        return true;
    }

    public boolean handleCraft(Player p, ItemStack itemClicked) {
        for (GameBehavior b : behaviors) {
            if (!b.onCraft(p, itemClicked)) {
                return false;
            }
        }
        return true;
    }

    public boolean handleDeath(Player p) {
        for (GameBehavior b : behaviors) {
            if (!b.onDeath(this, p)) {
                return false;
            }
        }
        return true;
    }

    public boolean handlePlayerDamage(Player p, EntityDamageEvent e) {
        for (GameBehavior b : behaviors) {
            if (!b.onPlayerDamage(p, e)) {
                return false;
            }
        }
        return true;
    }

    public boolean handleHit(Player damager, Player victim) {
        for (GameBehavior b : behaviors) {
            if (!b.onHit(damager, victim)) {
                return false;
            }
        }
        return true;
    }

    public boolean handlePlace(Player p, Block block) {
        for (GameBehavior b : behaviors) {
            if (!b.onPlace(p, block)) {
                return false;
            }
        }
        return true;
    }

    public boolean handleMove(Player p, Location to) {
        for (GameBehavior b : behaviors) {
            if (!b.onMove(this, p, to)) {
                return false;
            }
        }
        return true;
    }

    public void handleQuit(Player p) {
        behaviors.forEach(b -> b.onQuit(this, p));
    }

    public void handleEnd() {
        behaviors.forEach(b -> b.onEnd(this));
    }
}

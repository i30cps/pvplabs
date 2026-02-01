package dev.rotator.game;

import dev.rotator.PvPLabs;
import dev.rotator.util.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Game {
    protected final Location lowerCorner;
    protected final int xSize, zSize;
    protected final List<Player> players;

    protected Game(Location lowerCorner, int xSize, int zSize, List<Player> players) {
        this.lowerCorner = lowerCorner;
        this.xSize = xSize;
        this.zSize = zSize;
        this.players = players;
        PvPLabs.getMain().getGameManager().markInGame(players, this);
    }

    protected void endGame() {
        for (Player p : players) {
            p.getInventory().clear();
            PlayerUtils.clearPotionEffects(p);
            p.setGameMode(GameMode.ADVENTURE);
            p.teleport(PvPLabs.getMain().getSpawnLocation());
            p.setFireTicks(0);
            AttributeInstance attr = p.getAttribute(Attribute.MAX_HEALTH);
            if (attr != null) p.setHealth(attr.getValue());
        }
        PvPLabs.getMain().getGameManager().markNotInGame(players);
        PvPLabs.getMain().getGameManager().removeGame(this);
    }

    public abstract void start();

    public Location getLowerCorner() { return lowerCorner; }
    public int getXSize() { return xSize; }
    public int getZSize() { return zSize; }
    public List<Player> getPlayers() { return List.copyOf(players); }
}

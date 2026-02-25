package dev.rotator.pvplabs.game.behaviors;

import dev.rotator.pvplabs.PvPLabs;
import dev.rotator.pvplabs.game.Game;
import dev.rotator.pvplabs.game.GameBehavior;
import dev.rotator.pvplabs.game.GameMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class OutOfBoundsBehavior implements GameBehavior {

    private final GameMap map;
    private final Location lowerCorner;
    private final int seconds;
    private final Consumer<Player> elimination;

    private final Map<UUID, BukkitTask> tasks = new HashMap<>();

    public OutOfBoundsBehavior(GameMap map,
                               Location lowerCorner,
                               int seconds,
                               Consumer<Player> elimination) {
        this.map = map;
        this.lowerCorner = lowerCorner;
        this.seconds = seconds;
        this.elimination = elimination;
    }

    @Override
    public boolean onMove(Game game, Player p, Location to) {

        if (map.isInside(lowerCorner, to)) {
            BukkitTask task = tasks.remove(p.getUniqueId());
            if (task != null) task.cancel();
            return true;
        }

        if (tasks.containsKey(p.getUniqueId())) return true;

        p.sendMessage(ChatColor.RED + "Return to the arena within " + seconds + " seconds!");
        p.sendTitle(ChatColor.RED + "Return to the arena!", ChatColor.YELLOW + "Otherwise you will be eliminated in " + seconds + " seconds!", 5, 30, 5);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(
                PvPLabs.getMain(),
                () -> {
                    if (!map.isInside(lowerCorner, p.getLocation())) {
                        elimination.accept(p);
                    }
                    tasks.remove(p.getUniqueId());
                },
                20L * seconds
        );

        tasks.put(p.getUniqueId(), task);
        return true;
    }
}
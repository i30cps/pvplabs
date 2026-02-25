package dev.rotator.pvplabs.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class RedBlueNametagManager {

    private final Scoreboard scoreboard;
    private final Team red;
    private final Team blue;

    public RedBlueNametagManager(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.red = getOrCreateTeam("match_red", ChatColor.RED);
        this.blue = getOrCreateTeam("match_blue", ChatColor.BLUE);
    }

    public void putRedTeam(Player player) {
        blue.removeEntry(player.getName());
        red.addEntry(player.getName());
    }

    public void putBlueTeam(Player player) {
        red.removeEntry(player.getName());
        blue.addEntry(player.getName());
    }

    public void clear(Player player) {
        red.removeEntry(player.getName());
        blue.removeEntry(player.getName());
    }

    private Team getOrCreateTeam(String name, ChatColor color) {
        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }
        team.setColor(color);
        return team;
    }
}
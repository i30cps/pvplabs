package dev.rotator.pvplabs.ranked;

import fr.mrmicky.fastboard.FastBoard;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class MatchSidebar {

    private final FastBoard board;

    public MatchSidebar(Player player) {
        this.board = new FastBoard(player);
        board.updateTitle(ChatColor.GOLD + "PvPLabs Ranked");
    }

    public void update(String redName, Object redRating,
                       String blueName, Object blueRating, String mode) {
        board.updateLines(
                "",
                ChatColor.GRAY + "Mode: " + ChatColor.WHITE + mode,
                "",
                ChatColor.RED + redName
                        + ChatColor.WHITE + "  (" + redRating + ")",
                ChatColor.BLUE + blueName
                        + ChatColor.WHITE + "  (" + blueRating + ")",
                "",
                ChatColor.YELLOW + "discord.gg/txGzD5RKGF"
        );
    }

    public void delete() {
        board.delete();
    }
}
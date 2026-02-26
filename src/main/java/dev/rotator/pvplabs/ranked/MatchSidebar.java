package dev.rotator.pvplabs.ranked;

import dev.rotator.pvplabs.game.PvPLabsPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class MatchSidebar {
    private final Player p1, p2;
    private final PvPLabsPlayer gp1, gp2;
    private final int p1Rating, p2Rating;
    private final String mode;
    public MatchSidebar(MatchDuelGame game) {
        this.p1 = game.getP1();
        this.p2 = game.getP2();
        this.gp1 = game.getGp1();
        this.gp2 = game.getGp2();
        this.p1Rating = game.getP1DisplayedRating();
        this.p2Rating = game.getP2DisplayedRating();
        this.mode = game.getModeName();
    }

    private void updateSidebar(PvPLabsPlayer gp,
                               String blueName, int blueRating, int bluePing,
                               String redName, int redRating, int redPing,
                               String timeElapsed) {
        gp.setBoardContents(
                ChatColor.GRAY + "Mode: " + ChatColor.WHITE + mode,
                ChatColor.GRAY + "Time elapsed: " + timeElapsed,
                "",
                ChatColor.BLUE + blueName
                        + ChatColor.WHITE + " [" + blueRating + "] (" + redPing + "ms)",
                ChatColor.RED + redName
                        + ChatColor.WHITE + " [" + redRating + "] (" + bluePing + "ms)"
        );
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            // H:MM:SS
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            // M:SS
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public void update(int secondsElapsed) {
        String formattedTime = formatTime(secondsElapsed);
        updateSidebar(gp1,
                p1.getName(), p1Rating, p1.getPing(),
                p2.getName(), p2Rating, p2.getPing(),
                formattedTime);
        updateSidebar(gp2,
                p2.getName(), p2Rating, p2.getPing(),
                p1.getName(), p1Rating, p1.getPing(),
                formattedTime);
    }
}

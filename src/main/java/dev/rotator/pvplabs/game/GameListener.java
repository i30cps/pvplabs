package dev.rotator.pvplabs.game;

import dev.rotator.pvplabs.PvPLabs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = GamePlayer.getPlayer(p);

        PvPLabs.getMain().getGameManager().markOnline(gp, false);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = GamePlayer.getPlayer(p);

        PvPLabs.getMain().getGameManager().markOnline(gp, true);

        if (gp.inLobby()) return;

        // TODO: Figure out how to handle joining in the middle of a game.
    }
}

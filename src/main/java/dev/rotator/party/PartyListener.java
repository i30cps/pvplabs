package dev.rotator.party;

import dev.rotator.PvPLabs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PartyListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        PvPLabs.getMain().getPartyManager().onLeaveGame(e.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        PvPLabs.getMain().getPartyManager().onJoinGame(e.getPlayer());
    }
}

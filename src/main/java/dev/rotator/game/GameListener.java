package dev.rotator.game;

import dev.rotator.PvPLabs;
import dev.rotator.game.behaviors.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = GamePlayer.getPlayer(p);

        PvPLabs.getMain().getGameManager().markOnline(gp, false);

        Game game = PvPLabs.getMain().getGameManager().getGame(p);
        if (game == null) return;

        if (game instanceof QuitBehavior b) {
            b.onQuit(p);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = GamePlayer.getPlayer(p);

        PvPLabs.getMain().getGameManager().markOnline(gp, true);

        if (gp.inLobby()) return;

        // TODO: Figure out how to handle joining in the middle of a game.
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Game game = PvPLabs.getMain().getGameManager().getGame(p);
        if (game == null) return;

        if (game instanceof BreakBlockBehavior b) {
            if (!b.onBreak(p, e.getBlock()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Game game = PvPLabs.getMain().getGameManager().getGame(p);
        if (game == null) return;

        if (game instanceof PlaceBlockBehavior b) {
            if (!b.onPlace(p, e.getBlock()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        Player damager = null;
        if (e instanceof EntityDamageByEntityEvent ede) {
            if (ede.getDamager() instanceof Player d) damager = d;
        }

        Game game = PvPLabs.getMain().getGameManager().getGame(p);
        if (game == null) return;

        if (game instanceof DamageBehavior b) {
            if (!b.onPlayerDamage(p, e.getCause(), e.getFinalDamage()))
                e.setCancelled(true);
        }

        if (damager != null && game instanceof HitBehavior b) {
            if (!b.onHit(damager, p))
                e.setCancelled(true);
        }

        if (game instanceof DeathBehavior b) {
            double finalHealth = p.getHealth() - e.getFinalDamage();
            if (finalHealth <= 0) {
                if (!b.onPlayerDeath(p))
                    e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Game game = PvPLabs.getMain().getGameManager().getGame(p);
        if (game == null) return;

        if (game instanceof MoveBehavior b) {
            if (!b.onMove(p, e.getTo()))
                e.setCancelled(true);
        }
    }
}

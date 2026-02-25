package dev.rotator.pvplabs.game;

import dev.rotator.pvplabs.game.behaviors.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameBehaviorListener implements Listener {
    private final GameManager gameManager;

    public GameBehaviorListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        Game game = gameManager.getGame(p);
        if (game == null) return;

        game.handleQuit(p);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Game game = gameManager.getGame(p);
        if (game == null) return;

        if (!game.handleBreak(p, e.getBlock())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Game game = gameManager.getGame(p);
        if (game == null) return;

        if (!game.handlePlace(p, e.getBlock()))  e.setCancelled(true);
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        Game game = gameManager.getGame(p);
        if (game == null) return;

        if (!game.handleCraft(p, e.getCurrentItem()))  e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        Player damager = null;
        if (e instanceof EntityDamageByEntityEvent ede) {
            if (ede.getDamager() instanceof Player d) damager = d;
        }

        Game game = gameManager.getGame(p);
        if (game == null) return;

        if (!game.handlePlayerDamage(p, e))
            e.setCancelled(true);

        if (damager != null) {
            if (!game.handleHit(p, damager)) e.setCancelled(true);
        }

        double finalHealth = p.getHealth() - e.getFinalDamage();
        if (finalHealth <= 0) {
            if (!game.handleDeath(p))
                e.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Game game = gameManager.getGame(p);
        if (game == null) return;

        if (!game.handleMove(p, e.getTo())) e.setCancelled(true);
    }
}

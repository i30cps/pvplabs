package dev.rotator.pvplabs.game;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public interface GameBehavior {
    default boolean onBreak(Player p, Block block) { return true; }

    default boolean onCraft(Player p, ItemStack itemClicked) { return true; }

    default boolean onDeath(Game game, Player player) { return true; }

    default boolean onPlayerDamage(Player p, EntityDamageEvent event) { return true; }

    default boolean onHit(Player damager, Player victim) { return true; }

    default boolean onPlace(Player p, Block block) { return true; }

    default boolean onMove(Game game, Player player, Location to) { return true; }

    default void onQuit(Game game, Player player) {}

    default void onEnd(Game game) {}

    /**
     * Behaviors with lower priority values will run first.
     * @return The priority.
     */
    default int getPriority() { return 0; }
}
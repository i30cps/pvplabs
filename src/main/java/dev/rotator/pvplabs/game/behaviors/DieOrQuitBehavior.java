package dev.rotator.pvplabs.game.behaviors;

import dev.rotator.pvplabs.game.Game;
import dev.rotator.pvplabs.game.GameBehavior;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class DieOrQuitBehavior implements GameBehavior {
    private final Consumer<Player> consumer;
    public DieOrQuitBehavior(Consumer<Player> consumer) {
        this.consumer = consumer;
    }

    @Override
    public boolean onDeath(Game game, Player player) {
        consumer.accept(player);
        return false;
    }

    @Override
    public void onQuit(Game game, Player player) {
        consumer.accept(player);
    }
}

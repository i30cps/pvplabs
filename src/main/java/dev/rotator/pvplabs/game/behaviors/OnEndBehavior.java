package dev.rotator.pvplabs.game.behaviors;

import dev.rotator.pvplabs.game.Game;
import dev.rotator.pvplabs.game.GameBehavior;

public class OnEndBehavior implements GameBehavior {
    private final Runnable onEndTask;

    public OnEndBehavior(Runnable onEndTask) {
        this.onEndTask = onEndTask;
    }

    @Override
    public void onEnd(Game game) {
        onEndTask.run();
    }
}

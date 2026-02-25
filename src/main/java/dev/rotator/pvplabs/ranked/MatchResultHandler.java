package dev.rotator.pvplabs.ranked;

import org.bukkit.entity.Player;

public interface MatchResultHandler {
    void onFinish(Player winner, Player loser);
}

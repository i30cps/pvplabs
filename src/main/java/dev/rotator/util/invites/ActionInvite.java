package dev.rotator.util.invites;

import dev.rotator.PvPLabs;
import dev.rotator.game.GamePlayer;
import dev.rotator.util.Pair;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * An invitation from a sender to a receiver for performing an action.
 */
public class ActionInvite {
    private static final Map<Pair<GamePlayer, GamePlayer>, Map<ActionInviteType, ActionInvite>> activeInvites = new HashMap<>();

    private final GamePlayer sender, receiver;
    private final ActionInviteHandler handler, onCancel;
    private final ActionInviteType type;
    private final BukkitTask cancelTask;

    private ActionInvite(ActionInviteType type, GamePlayer sender, GamePlayer receiver, ActionInviteHandler handler, ActionInviteHandler onCancel, long expiryTicks) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.handler = handler;
        this.onCancel = onCancel;

        cancelTask = new BukkitRunnable() {
            @Override
            public void run() {
                ActionInvite.this.cancel();
            }
        }.runTaskLater(PvPLabs.getMain(), expiryTicks);

        getActionInviteMap(sender, receiver).put(type, this);
    }

    /**
     * Creates a new ActionInvite, if there has not been one before.
     * @param type The type.
     * @param sender The invite sender.
     * @param receiver The invite receiver.
     * @param handler The handler.
     * @param expiryTicks the time it takes for the invite to expire.
     * @return the success of the creation of the invite (false means already exists)
     */
    public static boolean createInvite(ActionInviteType type, GamePlayer sender, GamePlayer receiver, ActionInviteHandler handler, ActionInviteHandler onCancel, long expiryTicks) {
        if (getInvite(type, sender, receiver) != null) return false; // already exists

        new ActionInvite(type, sender, receiver, handler, onCancel, expiryTicks);

        return true;
    }

    public static boolean hasInvite(ActionInviteType type, GamePlayer sender, GamePlayer receiver) {
        return getInvite(type, sender, receiver) != null;
    }

    /**
     * Accepts an active ActionInvite, if it exists.
     * @param type The type.
     * @param sender The sender.
     * @param receiver The receiver.
     * @return True if successful, false if unsuccessful (due to such an invitation not existing).
     */
    public static boolean acceptInvite(ActionInviteType type, GamePlayer sender, GamePlayer receiver) {
        ActionInvite inv = getInvite(type, sender, receiver);

        if (inv == null) return false;

        inv.getHandler().handle();
        inv.delete();

        return true;
    }

    private static ActionInvite getInvite(ActionInviteType type, GamePlayer sender, GamePlayer receiver) {
        Map<ActionInviteType, ActionInvite> m = getActionInviteMap(sender, receiver);
        if (m == null) return null;
        return m.get(type);
    }


    private static Map<ActionInviteType, ActionInvite> getActionInviteMap(GamePlayer sender, GamePlayer receiver) {
        return activeInvites.computeIfAbsent(new Pair<>(sender, receiver), k -> new HashMap<>());
    }

    public ActionInviteHandler getHandler() {
        return handler;
    }

    /**
     *  Runs onCancel and then deletes.
     */
    void cancel() {
        onCancel.handle();
        delete();
    }

    void delete() {
        getActionInviteMap(sender, receiver).remove(type);
        if (!cancelTask.isCancelled()) cancelTask.cancel();
    }

    @Override
    public String toString() {
        return "{ Invite: [" + type + "] " + sender.toString() + " -> " + receiver.toString() + " }";
    }

    public static String allInviteStringsDebug() {
        String result = "";
        for (Pair<GamePlayer, GamePlayer> p : activeInvites.keySet()) {
            result += p.getFirst().getName() + ", " + p.getSecond().getName() + "\n";
            for (ActionInviteType type : activeInvites.get(p).keySet()) {
                result += " - " + activeInvites.get(p).get(type) + "\n";
            }
        }
        return result;
    }
}

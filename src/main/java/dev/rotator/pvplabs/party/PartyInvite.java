package dev.rotator.pvplabs.party;

import dev.rotator.pvplabs.game.PvPLabsPlayer;
import dev.rotator.pvplabs.PvPLabs;
import org.bukkit.scheduler.BukkitRunnable;

public class PartyInvite {
    private final PvPLabsPlayer sender, receiver;
    private final PartyManager manager;
    private final Party party;

    public PartyInvite(PartyManager manager, PvPLabsPlayer sender, PvPLabsPlayer receiver) {
        this.manager = manager;
        this.sender = sender;
        this.receiver = receiver;
        this.party = manager.getParty(sender);

        new BukkitRunnable() {
            @Override
            public void run() {
                PartyInvite.this.cancel();
            }
        }.runTaskLater(PvPLabs.getMain(), 20*60L);
    }

    public void cancel() {
        manager.cancelInvite(this);
    }

    public PvPLabsPlayer getSender() {
        return sender;
    }

    public PvPLabsPlayer getReceiver() {
        return receiver;
    }

    public Party getParty() {
        return party;
    }

    @Override
    public String toString() {
        return getSender().toString() + " -> " + getReceiver().toString();
    }
}

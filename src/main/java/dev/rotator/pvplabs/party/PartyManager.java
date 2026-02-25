package dev.rotator.pvplabs.party;

import dev.rotator.pvplabs.game.GamePlayer;
import dev.rotator.pvplabs.PvPLabs;
import dev.rotator.pvplabs.util.Pair;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

/**
 * The manager class for the party system, handles all the game logic.
 * If the party system is viewed as an MVC model, then this class would be the controller.
 */
public class PartyManager {
    private final ArrayList<Party> parties;
    private final Map<GamePlayer, Party> playerPartyCache;
    private final Map<Pair<GamePlayer, GamePlayer>, PartyInvite> activeInvites = new HashMap<>();
    private final Map<UUID, BukkitTask> leaveTimers = new HashMap<>();

    public PartyManager() {
        parties = new ArrayList<>();
        playerPartyCache = new HashMap<>();
    }

    @Nullable
    public Party getParty(GamePlayer gp) {
        return playerPartyCache.get(gp);
    }

    @Nullable
    public Party getParty(Player p) {
        return getParty(GamePlayer.getPlayer(p));
    }

    public boolean hasParty(GamePlayer gp) {
        return playerPartyCache.containsKey(gp);
    }

    public boolean hasParty(Player p) {
        return hasParty(GamePlayer.getPlayer(p));
    }

    protected void broadcastToParty(Party party, String msg) {
        for (GamePlayer gp : party.getMembersListClone()) {
            Player p = Bukkit.getPlayer(gp.getUUID());
            if (p != null) p.sendMessage(msg);
        }
    }

    private Party createParty(GamePlayer owner) {
        Party party = new Party(owner);
        parties.add(party);
        playerPartyCache.put(owner, party);
        return party;
    }

    /**
     * Creates a party invite. Also creates a party if the player does not have a party.
     *
     * @param senderPlayer The sender of the party invite.
     * @param receiverPlayer The receiver of the party invite.
     * @return false if the invite sending failed due to insufficient permissions or an already existing invite, true otherwise.
     */
    public String sendInvite(@NotNull Player senderPlayer, @NotNull Player receiverPlayer) {
        PvPLabs.getMain().getLogger().info("Sending party invite from " + senderPlayer.getName() + " to " + receiverPlayer.getName());

        GamePlayer sender = GamePlayer.getPlayer(senderPlayer);
        GamePlayer receiver = GamePlayer.getPlayer(receiverPlayer);

        if (getInvite(sender, receiver) != null) return "You already have an invite to that player.";

        Party party = getParty(sender);
        if (party == null) party = createParty(sender);

        if (!party.getOwner().equals(sender)) return "You do not have permissions to invite players.";

        addInvite(sender, receiver);

        return "";
    }

    /**
     * Accepts party invite.
     *
     * @param sender The sender of the party invitation.
     * @param receiver The receiver of the party invitation.
     * @return An empty string if successful; otherwise, the error message as to why the party invite could not be accepted.
     */
    public String acceptInvite(GamePlayer sender, GamePlayer receiver) {
        PvPLabs.getMain().getLogger().info("Accepting party invite from " + sender.getName() + " to " + receiver.getName());
        PartyInvite invite = getInvite(sender, receiver);

        if (invite == null) return "No such invite was found.";
        if (hasParty(receiver)) return "You are already in a party.";

        cancelInvite(invite);

        Party party = getParty(sender);

        if (party == null) return "That player no longer has a party.";
        if (!party.isOwner(sender)) return "That player is not the owner of a party anymore.";

        party.addMember(receiver);
        playerPartyCache.put(receiver, party);

        return "";
    }

    PartyInvite getInvite(GamePlayer sender, GamePlayer receiver) {
        PartyInvite inv = activeInvites.get(new Pair<>(sender, receiver));
        if (inv == null) {
            PvPLabs.getMain().getLogger().info("getInvite null");
        }
        else PvPLabs.getMain().getLogger().info("getInvite " + inv);
        return activeInvites.get(new Pair<>(sender, receiver));
    }

    void addInvite(GamePlayer sender, GamePlayer receiver) {
        PvPLabs.getMain().getLogger().info("Adding party invite from " + sender.getName() + " to " + receiver.getName());
        activeInvites.put(new Pair<>(sender, receiver), new PartyInvite(this, sender, receiver));
    }

    public void cancelInvite(PartyInvite invite) {
        PvPLabs.getMain().getLogger().info("Cancelling party invite from " + invite.getSender().getName() + " to " + invite.getReceiver().getName());
        activeInvites.remove(new Pair<>(invite.getSender(), invite.getReceiver()));
    }

    public List<Party> getPartyListCopy() {
        return new ArrayList<>(parties);
    }

    protected void onLeaveGame(Player p) {
        Party party = getParty(p);
        GamePlayer gp = GamePlayer.getPlayer(p);

        if (party == null) return;

        broadcastToParty(party, ChatColor.BLUE + "[Party] " + p.getName() + " left the game, has 5 minutes to reconnect before being kicked.");

        leaveTimers.put(p.getUniqueId(), new BukkitRunnable() {
            public void run() {
                leaveParty(gp);
            }
        }.runTaskLater(PvPLabs.getMain(), 20L*60*5));
    }

    protected void onJoinGame(Player p) {
        BukkitTask task = leaveTimers.get(p.getUniqueId());
        if (task != null) task.cancel();
    }

    /**
     * Removes a player from their party, if they are in one.
     * Also sends a message to party members to notify them of who the new owner is, if applicable.
     *
     * @param gp The GamePlayer gp.
     * @return The message to be sent to the player who left the party (may not be applicable to certain uses)
     */
    public String leaveParty(GamePlayer gp) {
        Player p = Bukkit.getPlayer(gp.getUUID());
        Party party = getParty(gp);

        if (party == null) return "Player does not have a party.";

        party.removeMember(gp);
        playerPartyCache.remove(gp);

        if (party.isOwner(gp)) {
            if (party.size() == 0) {
                disbandParty(party);
                return "Disbanded party.";
            } else {
                GamePlayer newOwner = party.getMembersListClone().getFirst();
                party.setOwner(newOwner);
                // TODO: Move messaging out of PartyManager.
                broadcastToParty(party, ChatColor.BLUE + "[Party] " + ChatColor.WHITE
                                + newOwner.getName() + ChatColor.BLUE
                                + " is the new owner (previous party owner left).");
                return "Left party; new owner is now " + newOwner.getName();
            }
        }

        return "Party leave successful!";
    }

    /**
     * Deletes a party and removes all references to it.
     * Also sends the appropriate chat messages for this action.
     *
     * @param party The party to disband.
     */
    public void disbandParty(Party party) {
        // TODO: Move message sending to be handled elsewhere (otu of PartyManager).
        for (GamePlayer gp : party.getMembersListClone()) {
            Player p = Bukkit.getPlayer(gp.getUUID());
            if (p != null) p.sendMessage(ChatColor.BLUE + "[Party] The party has been disbanded.");
        }
        parties.remove(party);
        for (GamePlayer key : new ArrayList<>(playerPartyCache.keySet())) {
            if (playerPartyCache.get(key).equals(party)) leaveParty(key);
        }
        for (Pair<GamePlayer, GamePlayer> pair : new ArrayList<>(activeInvites.keySet())) {
            if (activeInvites.get(pair).getParty().equals(party)) activeInvites.remove(pair);
        }
    }

    public ArrayList<Player> getPartyPlayers(Party party) {
        return new ArrayList<>(party.getMembersListClone().stream()
                .map(gp -> Bukkit.getPlayer(gp.getUUID()))
                .toList()); // toList() returns an immutable list in Java 16+, otherwise use .collect(Collectors.toList())
    }

}

package dev.rotator.party;

import dev.rotator.game.GamePlayer;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Party {
    private final ArrayList<GamePlayer> members;
    private GamePlayer owner;

    protected Party(GamePlayer owner) {
        members = new ArrayList<>();

        this.owner = owner;
        members.add(owner);
    }

    public boolean isOwner(GamePlayer gp) {
        return owner.equals(gp);
    }

    /**
     * Check if the party is ready to perform an action that requires all members to be online and not busy.
     *
     * @return true if all members are online and in a lobby, false otherwise.
     */
    public boolean isReady() {
        return members.stream().allMatch(GamePlayer::isReady);
    }

    public GamePlayer getOwner() {
        return owner;
    }

    /**
     * Set the owner. NoSuchElementException will be thrown if the owner is not in the party.
     *
     * @param gp the GamePlayer.
     */
    public void setOwner(GamePlayer gp) {
        if (!members.contains(gp))
            throw new NoSuchElementException("Owner is not in party");
        owner = gp;
    }

    public ArrayList<GamePlayer> getMembersListClone() {
        return new ArrayList<>(members);
    }

    public void addMember(GamePlayer gp) {
        members.add(gp);
    }

    /**
     * Remove a member.
     * @param gp The GamePlayer member to be removed
     * @return true if the list of members contained the member to be removed.
     */
    public boolean removeMember(GamePlayer gp) {
        return members.remove(gp);
    }

    public int size() {
        return members.size();
    }
}
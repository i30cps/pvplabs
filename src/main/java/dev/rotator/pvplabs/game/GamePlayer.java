package dev.rotator.pvplabs.game;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayer {
    private static final Map<UUID, GamePlayer> CACHE = new HashMap<>();
    private final UUID uuid;
    private String name;
    private boolean online, isInLobby, fallDamage;

    /**
     * Gets the GamePlayer object specific for the UUID, so that no duplicate objects will be created.
     *
     * @param p The player.
     * @return The GamePlayer object for that specific UUID.
     */
    public static GamePlayer getPlayer(Player p) {
        if (CACHE.containsKey(p.getUniqueId())) return CACHE.get(p.getUniqueId());
        return new GamePlayer(p.getUniqueId(), p.getName());
    }

    private GamePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.online = true;
        this.isInLobby = true;
        this.fallDamage = true;
        CACHE.put(uuid, this);
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Check if the GamePlayer is online.
     * @return true if player with the uuid is online, false if the player with the uuid is not.
     */
    public boolean isOnline() {
        return online;
    }

    public void setFallDamage(boolean value) {
        fallDamage = value;
    }

    @Deprecated
    public boolean getFallDamage() {
        return fallDamage;
    }


    /**
     * Check if the GamePlayer is in a lobby state.
     * @return true if the player with the uuid is in a lobby, false if otherwise.
     */
    public boolean inLobby() {
        return isInLobby;
    }

    protected void markInLobby(boolean value) {
        isInLobby = value;
    }

    /**
     * Check if GamePlayer is both online and in a lobby state.
     * @return The result of applying the "and" (&&) operator to the results of using the isOnline and inLobby methods.
     */
    public boolean isReady() {
        return isOnline() && inLobby();
    }

    public UUID getUUID() {
        return uuid;
    }

    /**
     * Cleans up memory by removing the instance of this particular GamePlayer object.
     */
    @Deprecated
    public void forget() {
        // TODO: Figure out how to deal with other classes having references to this GamePlayer.
        CACHE.remove(this.uuid);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        String result = name;

        if (!isOnline()) result += " (Offline)";
        if (!inLobby()) result += " (In Game)";

        return result;
    }

    public String getStateString() {
        String result = "";

        if (!isOnline()) result += " (Offline)";
        if (!inLobby()) result += " (In Game)";
        if (isReady()) result = " (Ready)";

        return result;
    }

    protected void markOnline(boolean online) {
        this.online = online;
    }
}

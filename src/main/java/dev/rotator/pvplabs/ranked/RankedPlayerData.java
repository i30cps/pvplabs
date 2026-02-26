package dev.rotator.pvplabs.ranked;


import java.util.UUID;

public class RankedPlayerData {
    private final UUID uuid;
    private int displayedRating;
    public  RankedPlayerData(UUID uuid) {
        this.uuid = uuid;
        displayedRating = 67; // temporary placeholder
    }

    public UUID getUuid() {
        return uuid;
    }
    
    public int getDisplayedRating() {
        return displayedRating;
    }
}

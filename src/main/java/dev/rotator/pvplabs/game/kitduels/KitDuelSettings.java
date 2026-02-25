package dev.rotator.pvplabs.game.kitduels;

public class KitDuelSettings {

    private boolean friendlyFire = false;
    private boolean canBreakMap = false;
    private boolean canCraft = false;
    private int firstTo = 1;
    private boolean itemDrops = true;
    private int glowTime = -1;

    public boolean friendlyFire() {
        return friendlyFire;
    }

    public boolean canBreakMap() {
        return canBreakMap;
    }

    public boolean canCraft() {
        return canCraft;
    }

    public int firstTo() {
        return firstTo;
    }

    public boolean itemDrops() {
        return itemDrops;
    }

    public int glowTime() {
        return glowTime;
    }

    public KitDuelSettings friendlyFire(boolean value) {
        this.friendlyFire = value;
        return this;
    }

    public KitDuelSettings canBreakMap(boolean value) {
        this.canBreakMap = value;
        return this;
    }

    public KitDuelSettings canCraft(boolean value) {
        this.canCraft = value;
        return this;
    }

    public KitDuelSettings itemDrops(boolean value) {
        this.itemDrops = value;
        return this;
    }

    public KitDuelSettings glowTime(int value) {
        this.glowTime = value;
        return this;
    }

    public KitDuelSettings firstTo(int value) {
        this.firstTo = value;
        return this;
    }
}
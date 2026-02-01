package dev.rotator.game.kitduels;

public class KitDuelSettings {

    private boolean friendlyFire = false;
    private boolean canBreakMap = false;
    private boolean canCraft = false;

    public boolean friendlyFire() {
        return friendlyFire;
    }

    public boolean canBreakMap() {
        return canBreakMap;
    }

    public boolean canCraft() {
        return canCraft;
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
}
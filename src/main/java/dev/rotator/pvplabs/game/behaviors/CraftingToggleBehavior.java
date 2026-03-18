package dev.rotator.pvplabs.game.behaviors;

import dev.rotator.pvplabs.game.GameBehavior;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CraftingToggleBehavior implements GameBehavior {
    private boolean allowCrafting;
    public CraftingToggleBehavior(boolean allowCrafting) {
        this.allowCrafting = allowCrafting;
    }

    public void setAllowCrafting(boolean allowCrafting) {
        this.allowCrafting = allowCrafting;
    }

    @Override
    public boolean onCraft(Player p, ItemStack clicked) {
        return allowCrafting;
    }
}

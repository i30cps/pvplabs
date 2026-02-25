package dev.rotator.pvplabs.customitems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public abstract class CustomItem {
    final String id;

    public CustomItem(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    // method to create the actual ItemStack
    public abstract ItemStack createItem();

    /** Cooldown in milliseconds. Default: none */
    public long getCooldownMillis() {
        return 0;
    }

    /** Called when cooldown blocks usage */
    protected void onCooldown(Player player, long remainingMillis) {
        // default: do nothing
    }

    /**
     * Called whenever a PlayerInteractEvent right click event occurs with
     * this custom item as the held item and the item is NOT ON COOLDOWN.
     * @param player The player using the item/
     * @param item The specific custom item used here.
     */
    protected abstract void onUseImpl(Player player, ItemStack item);

    /** Final entry point */
    public final void onUse(Player player, ItemStack item) {
        if (!CooldownManager.tryUse(player.getUniqueId(), id, getCooldownMillis())) {
            long remaining = CooldownManager.getRemaining(player.getUniqueId(), id);
            onCooldown(player, remaining);
            return;
        }
        onUseImpl(player, item);
    }

    /**
     * @param stack The item.
     * @return Whether the item is this particular custom item
     */
    public boolean isItem(ItemStack stack) {
        if (stack == null) return false;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;
        String stackId = meta.getPersistentDataContainer()
                .get(CustomItemsManager.KEY, PersistentDataType.STRING);
        return id.equals(stackId);
    }
}

package dev.rotator.pvplabs.customitems;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CustomItemsListener implements Listener {
    private final CustomItemsManager manager;

    public CustomItemsListener(CustomItemsManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (manager.getCustomItem(e.getItemInHand()) != null)
            e.setCancelled(true);
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        ItemStack item = event.getItem();
        CustomItem ci = manager.getCustomItem(item);
        if (ci == null) return;

        ci.onUse(event.getPlayer(), item);
        event.setCancelled(true);
    }
}

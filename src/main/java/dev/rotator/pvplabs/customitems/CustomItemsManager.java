package dev.rotator.pvplabs.customitems;

import dev.rotator.pvplabs.PvPLabs;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CustomItemsManager {
    protected final static NamespacedKey KEY = new NamespacedKey(PvPLabs.getMain(), "custom_items");
    private final Map<String, CustomItem> items = new HashMap<>();

    public CustomItemsManager() {
        register(new HopliteGoldenHeadItem("golden_head"));
    }

    private void register(CustomItem ci) {
        items.put(ci.getId(), ci);
    }

    @Nullable
    public ItemStack createItem(String id) {
        CustomItem item = items.get(id);
        if (item == null) return null;
        return item.createItem();
    }

    @Nullable
    public CustomItem getCustomItem(ItemStack stack) {
        if (stack == null) return null;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();


        String id = pdc.get(KEY, PersistentDataType.STRING);
        if (id == null) return null;

        return items.get(id);
    }
}

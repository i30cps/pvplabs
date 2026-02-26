package dev.rotator.pvplabs.kit;

import dev.rotator.pvplabs.util.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Kit {
    private final ItemStack[] inventory, armor;
    private final ItemStack offhand;
    private final List<Map<String, Object>> effects;
    private final String name;

    public Kit(String name, ItemStack[] inventory, ItemStack[] armor, ItemStack offhand, List<Map<String, Object>> effects) {
        this.name = name;
        this.inventory = inventory;
        this.armor = armor;
        this.offhand = offhand;
        this.effects = effects;
    }

    public void apply(Player p) {
        p.getInventory().setContents(cloneInventory(inventory));
        p.getInventory().setArmorContents(cloneInventory(armor));
        p.getInventory().setItemInOffHand(offhand.clone());

        PlayerUtils.clearPotionEffects(p);

        for (Map<String, Object> map : effects) {
            PotionEffectType type = PotionEffectType.getByName((String) map.get("type"));
            if (type == null) continue;

            p.addPotionEffect(new PotionEffect(
                    type,
                    (int) map.get("duration"),
                    (int) map.get("amplifier"),
                    (boolean) map.get("ambient"),
                    (boolean) map.get("particles"),
                    (boolean) map.get("icon")
            ));
        }
    }

    private ItemStack[] cloneInventory(ItemStack[] inventory) {
        return Arrays.stream(inventory)
                .map(item -> item == null ? null : item.clone())
                .toArray(ItemStack[]::new);
    }

    public String getName() {
        return name;
    }
}

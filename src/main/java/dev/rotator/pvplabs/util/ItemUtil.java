package dev.rotator.pvplabs.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {
    public static void smartGive(Player p, ItemStack itemStack) {
        Material m = itemStack.getType();
        PlayerInventory inv = p.getInventory();

        if (itemStack.getType().equals(inv.getItemInOffHand().getType())
                && itemStack.getAmount() + inv.getItemInOffHand().getAmount() <= 64) {
            inv.getItemInOffHand().setAmount(inv.getItemInOffHand().getAmount() + itemStack.getAmount());
            return;
        }


        if (m.toString().toLowerCase().contains("boots")) {
            inv.setBoots(itemStack);
            return;
        }
        if (m.toString().toLowerCase().contains("chestplate")) {
            inv.setChestplate(itemStack);
            return;
        }
        if (m.toString().toLowerCase().contains("leggings")) {
            inv.setLeggings(itemStack);
            return;
        }
        if (m.toString().toLowerCase().contains("helmet")) {
            inv.setHelmet(itemStack);
            return;
        }

        inv.addItem(itemStack);
    }

    public static String getName(ItemStack is) {
        ItemMeta meta = is.getItemMeta();
        if (meta != null && meta.hasDisplayName()) return meta.getDisplayName();
        return getFriendlyMaterialName(is.getType());
    }

    public static String getFriendlyMaterialName(Material material) {
        // Convert the enum constant to a human-readable name
        String name = material.name();
        // Replace underscores with spaces and capitalize the first letter of each word
        String[] words = name.split("_");
        StringBuilder friendlyName = new StringBuilder();
        for (String word : words) {
            friendlyName.append(word.substring(0, 1).toUpperCase());
            friendlyName.append(word.substring(1).toLowerCase());
            friendlyName.append(" ");
        }
        return friendlyName.toString().trim(); // Remove the last space
    }

    public static boolean removeMaterial(Player p, Material material, int amount) {
        Inventory inventory = p.getInventory();
        int totalItems = 0;

        // First, count the total number of items of the specified material
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                totalItems += item.getAmount();
            }
        }

        // If the player doesn't have enough items, return false
        if (totalItems < amount) {
            return false;
        }

        // Now, start removing the items from the inventory
        int amountToRemove = amount;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (item != null && item.getType() == material) {
                int itemAmount = item.getAmount();

                if (itemAmount <= amountToRemove) {
                    // Remove this entire stack
                    inventory.setItem(i, null);
                    amountToRemove -= itemAmount;
                } else {
                    // Reduce the stack size
                    item.setAmount(itemAmount - amountToRemove);
                    inventory.setItem(i, item);
                    amountToRemove = 0; // We have removed the required amount
                }

                // If we've removed enough, stop
                if (amountToRemove == 0) {
                    break;
                }
            }
        }

        return true;
    }
}

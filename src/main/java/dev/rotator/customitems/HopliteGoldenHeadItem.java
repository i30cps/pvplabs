package dev.rotator.customitems;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;

import java.util.List;
import java.util.UUID;

import static dev.rotator.customitems.CustomItemsManager.KEY;

public class HopliteGoldenHeadItem extends CustomItem {
    private static final UUID UHC_UUID = UUID.fromString("523f3491-5336-48e6-98b2-b5e1593b9423");

    public HopliteGoldenHeadItem(String id) {
        super(id);
    }

    public ItemStack createItem() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GOLD + "Golden Head");
        meta.setLore(List.of(ChatColor.YELLOW + "Right click to consume"));
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, id);

        PlayerProfile profile = Bukkit.createPlayerProfile(UHC_UUID);
        meta.setOwnerProfile(profile);

        head.setItemMeta(meta);
        return head;
    }

    @Override
    public long getCooldownMillis() {
        return 1000;
    }

    @Override
    protected void onCooldown(Player player, long remainingMillis) {
        player.sendMessage("§cYou're on cooldown for eating golden heads for " + remainingMillis + " milliseconds.");
    }

    public void onUseImpl(Player player, ItemStack item) {
        player.sendMessage("§aYou ate a §6Golden Head§a!");
        // Apply effects...
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10*20, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120*20, 1));

        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        // Decrease item count
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().removeItem(item);
        }

        player.getWorld().playSound(
                player, Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f
        );
    }

    public static NamespacedKey getKey() {
        return KEY;
    }
}

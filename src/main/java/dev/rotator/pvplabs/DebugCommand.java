package dev.rotator.pvplabs;

import dev.rotator.pvplabs.kit.InventoryIO;
import dev.rotator.pvplabs.ranked.MatchSidebar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class DebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.isOp()) return true;
        if (!(commandSender instanceof Player p)) return true;

        if (strings.length == 0) {
            p.sendMessage("§cPlease add arguments");
            return true;
        }

        if (strings[0].equals("kit")) {
            switch (strings[1]) {
                case "save" -> {
                    if (strings.length < 3) {
                        p.sendMessage("§c/rotatordebug kit save kitname");
                        return true;
                    }
                    File folder = new File(PvPLabs.getMain().getDataFolder(), "kits");
                    File file = new File(folder, strings[2] + ".yml");
                    try {
                        InventoryIO.savePlayerInventory(p, file);
                        p.sendMessage("success");
                    } catch (IOException e) {
                        p.sendMessage(e.getLocalizedMessage());
                    }
                }
                case "give" -> {
                    if (strings.length < 3) {
                        p.sendMessage("§c/rotatordebug kit give kitname");
                        return true;
                    }
                    PvPLabs.getMain().getKitManager().getKit(strings[2]).apply(p);
                    p.sendMessage("gave u kit sir");
                }
                case "refresh" -> PvPLabs.getMain().getKitManager().clearCache();
                default -> p.sendMessage("§c/rotatordebug kit save/give/refresh");
            }
        } else if (strings[0].equals("ghead")) {
            p.getInventory().addItem(PvPLabs.getMain().getCustomItemsManager().createItem("golden_head"));
        } else if (strings[0].equals("sidebar")) {
            new MatchSidebar(p).update("redName", 1, "blueName", 2, "mode");
        }

        return true;
    }
}

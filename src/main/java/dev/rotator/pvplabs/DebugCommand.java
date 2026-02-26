package dev.rotator.pvplabs;

import com.sk89q.worldedit.math.BlockVector3;
import dev.rotator.pvplabs.kit.InventoryIO;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
        } else if  (strings[0].equals("test_blockvector3")) {
            Set<BlockVector3> test = new HashSet<>();
            BlockVector3 a = BlockVector3.at(29, 19, 31);
            BlockVector3 b = BlockVector3.at(29, 19, 31);
            test.add(a);
            p.sendMessage("" + test.contains(b)); // must print true
            p.sendMessage("" + a.equals(b)); // must print true
            p.sendMessage("" + (a.hashCode() == b.hashCode())); // must print true
        }

        return true;
    }
}

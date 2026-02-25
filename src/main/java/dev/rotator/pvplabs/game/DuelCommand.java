package dev.rotator.pvplabs.game;

import dev.rotator.pvplabs.PvPLabs;
import dev.rotator.pvplabs.party.Party;
import dev.rotator.pvplabs.party.PartyManager;
import dev.rotator.pvplabs.util.invites.ActionInvite;
import dev.rotator.pvplabs.util.invites.ActionInviteType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DuelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player p)) return true;

        if (strings.length == 0) return false;
        Player p2 = Bukkit.getPlayer(strings[0]);

        if (p2 == null) {
            p.sendMessage(ChatColor.RED + "That player does not exist or is offline.");
            return true;
        }

        GamePlayer gp = GamePlayer.getPlayer(p);
        GamePlayer gp2 = GamePlayer.getPlayer(p2);

        if (strings.length == 2 && strings[1].equalsIgnoreCase("accept")) {
            if (!ActionInvite.acceptInvite(ActionInviteType.DUEL, gp2, gp)) {
                p.sendMessage(ChatColor.RED + "No such duel to accept was found.");
            }
            return true;
        }

        boolean success = ActionInvite.createInvite(ActionInviteType.DUEL, gp, gp2, () -> {
            p.sendMessage(ChatColor.YELLOW + "Invite to " + p2.getName() + " has been accepted.");
            p2.sendMessage(ChatColor.YELLOW + "Invite from " + p.getName() + " has been accepted.");

            PvPLabs.getMain().getGameManager().startMoleGame(p, p2);
        }, (() -> {
            String message = ChatColor.RED + "Duel invitation from " + gp.getName() + " to " + gp2.getName() + " has expired.";
            p.sendMessage(message);
            p2.sendMessage(message);
        }), 20*60L);

        if (success) {
            PvPLabs.getMain().adventure().player(p2).sendMessage(
                    Component.text(p.getName() + " has sent you a duel request. Click to accept")
                            .color(NamedTextColor.GREEN)
                            .clickEvent(ClickEvent.runCommand("duel " + p.getName() + " accept"))
                            .hoverEvent(HoverEvent.showText(
                                    Component.text("Click to accept the duel request!")
                                            .color(NamedTextColor.GREEN)
                            ))
            );
            p.sendMessage(ChatColor.YELLOW + p2.getName() + " has been sent a duel request.");
        } else {
            p.sendMessage(ChatColor.RED + "Could not create a duel invite, probably already exists a such invite.");
        }

        return true;
    }
}

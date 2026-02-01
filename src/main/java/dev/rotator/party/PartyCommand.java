package dev.rotator.party;

import dev.rotator.game.GamePlayer;
import dev.rotator.PvPLabs;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// Note, assertions are used here only to silence IDE warnings and increase code clarity.
public class PartyCommand implements CommandExecutor {
    // TODO: When moving messaging out of PartyManager, also take the messages out of PartyCommand, too.
    private void sendMsg(Player p, String message) {
        p.sendMessage(ChatColor.BLUE + "[Party] " + message);
    }

    private void sendErrMsg(Player p, String message) {
        p.sendMessage(ChatColor.RED + "[Party] Error: " + message);
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return true;
        }

        PartyManager partyManager = PvPLabs.getMain().getPartyManager();

        if (strings.length == 0) return false;

        Party party = partyManager.getParty(p);
        Player p2;

        switch(strings[0]) {
            case "list":
                if (!partyManager.hasParty(p)) {
                    sendErrMsg(p, "You are not in a party!");
                    return true;
                }

                sendMsg(p, "Current party members:");

                assert party != null;

                for (GamePlayer gp : party.getMembersListClone()) {
                    sendMsg(p, "  -> " + gp.getName() + ChatColor.WHITE + gp.getStateString());
                }
                break;
            case "invite":
                if (strings.length != 2) return false;

                p2 = Bukkit.getPlayer(strings[1]);

                if (p2 == null) {
                    sendErrMsg(p, "That player is offline or does not exist.");
                    return true;
                }

                String result = partyManager.sendInvite(p, p2);
                if (!result.isEmpty()) {
                    sendErrMsg(p, result);
                    return true;
                }

                sendMsg(p, "Sent party invite to " + ChatColor.AQUA + p2.getName());
                net.md_5.bungee.api.chat.TextComponent message = new TextComponent(
                        ChatColor.AQUA + p.getName() + ChatColor.YELLOW + " has sent you a party invite! Click to accept."
                );

                message.setClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/party accept " + p.getName()
                ));

                p2.spigot().sendMessage(message);

                break;
            case "accept":
                if (strings.length != 2) return false;

                p2 = Bukkit.getPlayer(strings[1]);

                if (p2 == null) {
                    sendErrMsg(p, "That player does not exist or is offline.");
                    return true;
                }
                
                String errMsg = partyManager.acceptInvite(GamePlayer.getPlayer(p2), GamePlayer.getPlayer(p));

                if (errMsg.isEmpty()) {
                    Party party2 = partyManager.getParty(p2);

                    assert party2 != null; // Since there was not an error message, this will always be not null.

                    for (GamePlayer gp : party2.getMembersListClone()) {
                        Player memberPlayer = Bukkit.getPlayer(gp.getUUID());
                        if (memberPlayer != null)
                            sendMsg(memberPlayer, ChatColor.WHITE + p.getName() + ChatColor.BLUE + " joined the party.");
                    }
                } else {
                    sendErrMsg(p, errMsg);
                }
                break;
            case "leave":
                sendMsg(p, partyManager.leaveParty(GamePlayer.getPlayer(p)));
                break;
            default:
                return false;
        }

        return true;
    }
}

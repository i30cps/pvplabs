package dev.rotator.game;

import dev.rotator.PvPLabs;
import dev.rotator.game.ctf.CTFMap;
import dev.rotator.game.ctf.StandardCTFGame;
import dev.rotator.game.kitduels.KitDuelGame;
import dev.rotator.game.kitduels.KitDuelMap;
import dev.rotator.kit.Kit;
import dev.rotator.party.Party;
import dev.rotator.party.PartyManager;
import dev.rotator.util.invites.ActionInvite;
import dev.rotator.util.invites.ActionInviteType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
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

        if (ActionInvite.acceptInvite(ActionInviteType.DUEL, gp2, gp)) return true;

        boolean success = ActionInvite.createInvite(ActionInviteType.DUEL, gp, gp2, () -> {
            p.sendMessage(ChatColor.YELLOW + "Invite to " + p2.getName() + " has been accepted.");
            p2.sendMessage(ChatColor.YELLOW + "Invite from " + p.getName() + " has been accepted.");

            ArrayList<Player> team1, team2;

            PartyManager manager = PvPLabs.getMain().getPartyManager();
            Party party = manager.getParty(gp);

            if (party == null) team1 = new ArrayList<>(List.of(p));
            else team1 = manager.getPartyPlayers(party);

            Party party2 = manager.getParty(gp2);

            if (party2 == null) team2 = new ArrayList<>(List.of(p2));
            else team2 = manager.getPartyPlayers(party2);

            PvPLabs.getMain().getGameManager().startMoleGame(team1, team2);
        }, (() -> {
            String message = ChatColor.RED + "Duel invitation from " + gp.getName() + " to " + gp2.getName() + " has expired.";
            p.sendMessage(message);
            p2.sendMessage(message);
        }), 20*60L);

        if (success) {
            p2.sendMessage(ChatColor.YELLOW + p.getName() + " has sent you a duel request. /duel them back to accept");
            p.sendMessage(ChatColor.YELLOW + p2.getName() + " has been sent a duel request.");
        } else {
            p.sendMessage(ChatColor.RED + "Could not create a duel invite, probably already exists a such invite.");
        }

        return true;
    }
}

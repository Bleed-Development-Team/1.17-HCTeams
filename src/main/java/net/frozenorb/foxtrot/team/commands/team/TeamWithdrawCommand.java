package net.frozenorb.foxtrot.team.commands.team;

import com.google.common.collect.ImmutableMap;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamWithdrawCommand {

    @Command(value={ "team withdraw", "t withdraw", "f withdraw", "faction withdraw", "fac withdraw", "team w", "t w", "f w", "faction w", "fac w" })
    public static void teamWithdraw(@Sender Player sender, @Name("amount") Float amount) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isOwner(sender.getUniqueId())) {
            if (team.getBalance() < amount) {
                sender.sendMessage(ChatColor.RED + "The team doesn't have enough money to do this!");
                return;
            }

            if (Double.isNaN(team.getBalance())) {
                sender.sendMessage(ChatColor.RED + "You cannot withdraw money because your team's balance is broken!");
                return;
            }

            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "You can't withdraw $0.0 (or less)!");
                return;
            }

            if (Float.isNaN(amount)) {
                sender.sendMessage(ChatColor.RED + "Nope.");
                return;
            }

            FrozenEconomyHandler.deposit(sender.getUniqueId(), amount);
            sender.sendMessage(ChatColor.YELLOW + "You have withdrawn " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " from the team balance!");

            TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_WITHDRAW_MONEY, ImmutableMap.of(
                    "playerId", sender.getUniqueId(),
                    "playerName", sender.getName(),
                    "amount", amount,
                    "oldBalance", team.getBalance(),
                    "newBalance", team.getBalance() - amount
            ));

            team.setBalance(team.getBalance() - amount);
            team.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + " withdrew " + ChatColor.LIGHT_PURPLE + "$" + amount + ChatColor.YELLOW + " from the team balance.");
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

}
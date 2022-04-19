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

public class TeamDepositCommand {

    @Command(value={ "team deposit", "t deposit", "f deposit", "faction deposit", "fac deposit", "team d", "t d", "f d", "faction d", "fac d", "team m d", "t m d", "f m d", "faction m d", "fac m d" })
    public static void teamDeposit(@Sender Player sender, @Name("amount") Float amount) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "You can't deposit $0.0 (or less)!");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage(ChatColor.RED + "Nope.");
            return;
        }

        if (FrozenEconomyHandler.getBalance(sender.getUniqueId()) < amount) {
            sender.sendMessage(ChatColor.RED + "You don't have enough money to do this!");
            return;
        }

        FrozenEconomyHandler.withdraw(sender.getUniqueId(), amount);

        sender.sendMessage(ChatColor.YELLOW + "You have added " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " to the team balance!");

        TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_DEPOSIT_MONEY, ImmutableMap.of(
                "playerId", sender.getUniqueId(),
                "playerName", sender.getName(),
                "amount", amount,
                "oldBalance", team.getBalance(),
                "newBalance", team.getBalance() + amount
        ));

        team.setBalance(team.getBalance() + amount);
        team.sendMessage(ChatColor.YELLOW + sender.getName() + " deposited " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " into the team balance.");

        Foxtrot.getInstance().getWrappedBalanceMap().setBalance(sender.getUniqueId(), FrozenEconomyHandler.getBalance(sender.getUniqueId()));
    }

    @Command(value={ "team deposit all", "t deposit all", "f deposit all", "faction deposit all", "fac deposit all", "team d all", "t d all", "f d all", "faction d all", "fac d all", "team m d all", "t m d all", "f m d all", "faction m d all", "fac m d all" })
    public static void teamDepositAll(@Sender Player sender) {
        teamDeposit(sender, (float) FrozenEconomyHandler.getBalance(sender.getUniqueId()));
    }

}
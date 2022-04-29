package net.frozenorb.foxtrot.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("setteambalance|setteambal")
@CommandPermission("foxtrot.team.setteambalance")
public class SetTeamBalanceCommand extends BaseCommand {


    @Default
    @Description("Set the team balance")
    public static void setTeamBalance(Player sender, @Name("team") Team team, @Name("amount") float balance) {
        team.setBalance(balance);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s balance is now " + ChatColor.LIGHT_PURPLE + team.getBalance() + ChatColor.YELLOW + ".");
    }

}
package net.frozenorb.foxtrot.team.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetTeamBalanceCommand {

    @Command(value={ "setteambalance", "setteambal" })
    @Permission(value = "foxtrot.setteambalance")
    public static void setTeamBalance(@Sender Player sender, @Name("team") Team team, @Name("balance") float balance) {
        team.setBalance(balance);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s balance is now " + ChatColor.LIGHT_PURPLE + team.getBalance() + ChatColor.YELLOW + ".");
    }

}
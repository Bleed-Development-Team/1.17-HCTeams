package net.frozenorb.foxtrot.map.stats.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.map.stats.StatsEntry;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("sm")
@CommandPermission("op")
public class StatModifyCommands extends BaseCommand {


	@Subcommand("setkills")
	public static void setKills(Player player, int kills) {
		StatsEntry stats = HCF.getInstance().getMapHandler().getStatsHandler().getStats(player);
		stats.setKills(kills);

		HCF.getInstance().getKillsMap().setKills(player.getUniqueId(), kills);

		player.sendMessage(ChatColor.GREEN + "You've set your own kills to: " + kills);
	}

	@Subcommand("setdeaths")
	public static void setDeaths(Player player, int deaths) {
		StatsEntry stats = HCF.getInstance().getMapHandler().getStatsHandler().getStats(player);
		stats.setDeaths(deaths);

		HCF.getInstance().getDeathsMap().setDeaths(player.getUniqueId(), deaths);

		player.sendMessage(ChatColor.GREEN + "You've set your own deaths to: " + deaths);
	}

	@Subcommand("setteamkills")
	public static void setTeamKills(Player player, int kills) {
		Team team = HCF.getInstance().getTeamHandler().getTeam(player);

		if (team != null) {
			team.setKills(kills);
			player.sendMessage(ChatColor.GREEN + "You've set your team's kills to: " + kills);
		}
	}

	@Subcommand("setteamdeaths")
	public static void setTeamDeaths(Player player, int deaths) {
		Team team = HCF.getInstance().getTeamHandler().getTeam(player);

		if (team != null) {
			team.setDeaths(deaths);
			player.sendMessage(ChatColor.GREEN + "You've set your team's deaths to: " + deaths);
		}
	}

}

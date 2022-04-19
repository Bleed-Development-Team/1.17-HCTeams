package net.frozenorb.foxtrot.map.stats.command;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.stats.StatsEntry;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StatModifyCommands {

	@Command(value = "sm setkills")
	@Permission(value = "op")
	public static void setKills(@Sender Player player, @Name("kills") int kills) {
		StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player);
		stats.setKills(kills);

		Foxtrot.getInstance().getKillsMap().setKills(player.getUniqueId(), kills);

		player.sendMessage(ChatColor.GREEN + "You've set your own kills to: " + kills);
	}

	@Command(value = "sm setdeaths")
	@Permission(value = "op")
	public static void setDeaths(@Sender Player player, @Name("deaths") int deaths) {
		StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player);
		stats.setDeaths(deaths);

		Foxtrot.getInstance().getDeathsMap().setDeaths(player.getUniqueId(), deaths);

		player.sendMessage(ChatColor.GREEN + "You've set your own deaths to: " + deaths);
	}

	@Command(value = "sm setteamkills")
	@Permission(value = "op")
	public static void setTeamKills(@Sender Player player, @Name("kills") int kills) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

		if (team != null) {
			team.setKills(kills);
			player.sendMessage(ChatColor.GREEN + "You've set your team's kills to: " + kills);
		}
	}

	@Command(value = "sm setteamdeaths")
	@Permission(value = "op")
	public static void setTeamDeaths(@Sender Player player, @Name("deaths") int deaths) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

		if (team != null) {
			team.setDeaths(deaths);
			player.sendMessage(ChatColor.GREEN + "You've set your team's deaths to: " + deaths);
		}
	}

}

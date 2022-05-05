package net.frozenorb.foxtrot.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Optional;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamPointBreakDownCommand extends BaseCommand {

	//@Command(value = { "team pointbr", "team pbr", "t pointbr", "t pbr" })//TODO Add this to the team command
	//@Permission(value = "op")
	public static void teamPointBreakDown(Player player, @Optional final Team team) {
		player.sendMessage(ChatColor.GOLD + "Point Breakdown of " + team.getName());

		for (String info : team.getPointBreakDown()) {
			player.sendMessage(info);
		}
	}

}

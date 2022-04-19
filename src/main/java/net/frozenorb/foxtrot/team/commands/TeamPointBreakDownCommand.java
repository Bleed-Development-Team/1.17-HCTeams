package net.frozenorb.foxtrot.team.commands;

import me.vaperion.blade.annotation.*;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamPointBreakDownCommand {

	@Command(value = { "team pointbr", "team pbr", "t pointbr", "t pbr" })
	@Permission(value = "op")
	public static void teamPointBreakDown(@Sender Player player, @Optional("self") final Team team) {
		player.sendMessage(ChatColor.GOLD + "Point Breakdown of " + team.getName());

		for (String info : team.getPointBreakDown()) {
			player.sendMessage(info);
		}
	}

}

package net.frozenorb.foxtrot.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.TeamHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("freezerosters")
@CommandPermission("op")
public class FreezeRostersCommand extends BaseCommand {


    @Default
    @Description("Freeze Rosters")
    public static void freezeRosters(Player sender) {
        TeamHandler teamHandler = Foxtrot.getInstance().getTeamHandler();
        teamHandler.setRostersLocked(!teamHandler.isRostersLocked());

        sender.sendMessage(ChatColor.YELLOW + "Team rosters are now " + ChatColor.LIGHT_PURPLE + (teamHandler.isRostersLocked() ? "locked" : "unlocked") + ChatColor.YELLOW + ".");
    }

}
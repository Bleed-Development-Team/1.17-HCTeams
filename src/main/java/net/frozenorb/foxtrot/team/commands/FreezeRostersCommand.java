package net.frozenorb.foxtrot.team.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.TeamHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FreezeRostersCommand {

    @Command(value={ "freezerosters" })
    @Permission(value = "op")
    public static void freezeRosters(@Sender Player sender) {
        TeamHandler teamHandler = Foxtrot.getInstance().getTeamHandler();
        teamHandler.setRostersLocked(!teamHandler.isRostersLocked());

        sender.sendMessage(ChatColor.YELLOW + "Team rosters are now " + ChatColor.LIGHT_PURPLE + (teamHandler.isRostersLocked() ? "locked" : "unlocked") + ChatColor.YELLOW + ".");
    }

}
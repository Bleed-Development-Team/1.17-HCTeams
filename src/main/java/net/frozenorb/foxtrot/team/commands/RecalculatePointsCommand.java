package net.frozenorb.foxtrot.team.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RecalculatePointsCommand {
    
    @Command(value = {"team recalculatepoints", "f recalculatepoints", "team recalcpoints"})
    @Permission(value = "op")
    public static void recalculate(@Sender CommandSender sender) {
        int changed = 0;
        
        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            int oldPoints = team.getPoints();
            team.recalculatePoints();
            if (team.getPoints() != oldPoints) {
                team.flagForSave();
                sender.sendMessage(ChatColor.YELLOW + "Changed " + team.getName() + "'s points from " + oldPoints + " to " + team.getPoints());
                changed++;
            }

        }
        
        sender.sendMessage(ChatColor.YELLOW + "Changed a total of " + changed + " teams points.");
    }
    
}

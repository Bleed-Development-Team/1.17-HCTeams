package net.frozenorb.foxtrot.team.commands;

import co.aikar.commands.BaseCommand;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RecalculatePointsCommand extends BaseCommand {//TODO Put this inside the team command base
    
    //@Command(value = {"team recalculatepoints", "f recalculatepoints", "team recalcpoints"})
   /// @Permission(value = "op")
    public static void recalculate(CommandSender sender) {
        int changed = 0;
        
        for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
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

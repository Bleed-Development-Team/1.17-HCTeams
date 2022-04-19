package net.frozenorb.foxtrot.team.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceLeaveCommand {

    @Command(value ={ "forceleave" })
    @Permission(value = "foxtrot.forceleave")
    public static void forceLeave(@Sender Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        team.removeMember(player.getUniqueId());
        Foxtrot.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);
        player.sendMessage(ChatColor.YELLOW + "Force left your team.");
    }

}
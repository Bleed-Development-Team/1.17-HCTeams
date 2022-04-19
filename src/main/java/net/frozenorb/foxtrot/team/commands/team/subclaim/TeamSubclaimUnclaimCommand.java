package net.frozenorb.foxtrot.team.commands.team.subclaim;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSubclaimUnclaimCommand {

    @Command(value={ "team subclaim unclaim", "t subclaim unclaim", "f subclaim unclaim", "faction subclaim unclaim", "fac subclaim unclaim", "team subclaim unsubclaim", "t subclaim unsubclaim", "f subclaim unsubclaim", "faction subclaim unsubclaim", "fac subclaim unsubclaim", "team unsubclaim", "t unsubclaim", "f unsubclaim", "faction unsubclaim", "fac unsubclaim"})
    public static void teamSubclaimUnclaim(@Sender Player sender, @Optional("subclaim") Subclaim subclaim) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            team.getSubclaims().remove(subclaim);
            LandBoard.getInstance().updateSubclaim(subclaim);
            team.flagForSave();
            sender.sendMessage(ChatColor.RED + "You have unclaimed the subclaim " + ChatColor.YELLOW + subclaim.getName() + ChatColor.RED + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Only team captains can unclaim subclaims!");
        }
    }

}
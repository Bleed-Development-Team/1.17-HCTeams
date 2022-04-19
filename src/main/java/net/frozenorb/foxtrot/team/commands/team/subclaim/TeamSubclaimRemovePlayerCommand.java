package net.frozenorb.foxtrot.team.commands.team.subclaim;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamSubclaimRemovePlayerCommand {

    @Command(value ={ "team subclaim removeplayer", "t subclaim removeplayer", "f subclaim removeplayer", "faction subclaim removeplayer", "fac subclaim removeplayer", "team sub removeplayer", "t sub removeplayer", "f sub removeplayer", "faction sub removeplayer", "fac sub removeplayer", "team subclaim revoke", "t subclaim revoke", "f subclaim revoke", "faction subclaim revoke", "fac subclaim revoke", "team sub revoke", "t sub revoke", "f sub revoke", "faction sub revoke", "fac sub revoke" })
    public static void teamSubclaimRemovePlayer(@Sender Player sender, @Name("subclaim") Subclaim subclaim, @Name("player") UUID player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the team captains can do this.");
            return;
        }

        if (!team.isMember(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is not on your team!");
            return;
        }

        if (!subclaim.isMember(player)) {
            sender.sendMessage(ChatColor.RED + "The player already does not have access to that subclaim!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " has been removed from the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + ".");
        subclaim.removeMember(player);
        team.flagForSave();
    }

}

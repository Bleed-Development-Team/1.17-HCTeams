package net.frozenorb.foxtrot.team;

import co.aikar.commands.*;
import co.aikar.commands.contexts.ContextResolver;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamType implements ContextResolver<Team, BukkitCommandExecutionContext> {

    @Override
    public Team getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        Player sender = arg.getPlayer();
        String source = arg.popFirstArg();

        if (sender != null && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Team team = HCF.getInstance().getTeamHandler().getTeam(sender.getUniqueId());

            if (team == null) {
                throw new InvalidCommandArgument(ChatColor.RESET + ChatColor.GRAY.toString() + "You're not on a team!");
            }

            return (team);
        }

        Team byName = HCF.getInstance().getTeamHandler().getTeam(source);

        if (byName != null) {
            return (byName);
        }

        Player bukkitPlayer = HCF.getInstance().getServer().getPlayer(source);

        if (bukkitPlayer != null) {
            Team byMemberBukkitPlayer = HCF.getInstance().getTeamHandler().getTeam(bukkitPlayer.getUniqueId());

            if (byMemberBukkitPlayer != null) {
                return (byMemberBukkitPlayer);
            }
        }

        Team byMemberUUID = HCF.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(source));

        if (byMemberUUID != null) {
            return (byMemberUUID);
        }

        throw new InvalidCommandArgument("No team or member with the name " + source + " found.");
    }	
}
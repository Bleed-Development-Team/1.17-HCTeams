package net.frozenorb.foxtrot.team;

import co.aikar.commands.*;
import co.aikar.commands.contexts.ContextResolver;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeamType implements ContextResolver<Team, BukkitCommandExecutionContext> {

    @Override
    public Team getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        Player sender = arg.getPlayer();
        String source = arg.getFirstArg();

        if (sender != null && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());

            if (team == null) {
                throw new InvalidCommandArgument(ChatColor.RESET + ChatColor.GRAY.toString() + "You're not on a team!");
            }

            return (team);
        }

        Team byName = Foxtrot.getInstance().getTeamHandler().getTeam(source);

        if (byName != null) {
            return (byName);
        }

        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(source);

        if (bukkitPlayer != null) {
            Team byMemberBukkitPlayer = Foxtrot.getInstance().getTeamHandler().getTeam(bukkitPlayer.getUniqueId());

            if (byMemberBukkitPlayer != null) {
                return (byMemberBukkitPlayer);
            }
        }

        Team byMemberUUID = Foxtrot.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(source));

        if (byMemberUUID != null) {
            return (byMemberUUID);
        }

        throw new InvalidCommandArgument("No team or member with the name " + source + " found.");
    }
}
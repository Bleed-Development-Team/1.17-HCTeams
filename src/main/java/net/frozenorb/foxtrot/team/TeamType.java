package net.frozenorb.foxtrot.team;

import me.vaperion.blade.argument.BladeArgument;
import me.vaperion.blade.argument.BladeProvider;
import me.vaperion.blade.context.BladeContext;
import me.vaperion.blade.exception.BladeExitMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeamType implements BladeProvider<Team> {

    @Override
    public @Nullable Team provide(@NotNull BladeContext context, @NotNull BladeArgument arg) throws BladeExitMessage {
        Player sender = context.sender().parseAs(Player.class);
        String source = arg.getString();
        if (sender != null && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());

            if (team == null) {
                throw new BladeExitMessage(ChatColor.RESET + ChatColor.GRAY.toString() + "You're not on a team!");
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

        throw new BladeExitMessage("No team or member with the name " + source + " found.");
    }

    @Override
    public @NotNull List<String> suggest(@NotNull BladeContext context, @NotNull BladeArgument argument) throws BladeExitMessage {
        List<String> completions = new ArrayList<>();

        // Teams being included in the completion is ENABLED by default.
        if (!argument.getString().contains("noteams")) {
            for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
                if (StringUtils.startsWithIgnoreCase(team.getName(), argument.getString())) {
                    completions.add(team.getName());
                }
            }
        }

        // Players being included in the completion is DISABLED by default.
        if (argument.getString().contains("players")) {
            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                if (StringUtils.startsWithIgnoreCase(player.getName(), argument.getString())) {
                    completions.add(player.getName());
                }
            }
        }

        return (completions);
    }
}
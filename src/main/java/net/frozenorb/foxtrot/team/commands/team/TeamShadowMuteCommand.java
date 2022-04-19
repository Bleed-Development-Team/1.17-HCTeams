package net.frozenorb.foxtrot.team.commands.team;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamShadowMuteCommand {

    @Getter public static Map<UUID, String> teamShadowMutes = new HashMap<>();

    @Command(value={ "team shadowmute", "t shadowmute", "f shadowmute", "faction shadowmute", "fac shadowmute" })
    @Permission(value = "foxtrot.mutefaction")
    public static void teamShadowMute(@Sender Player sender, @Name("team") final Team team, @Name("time") int time) {
        int timeSeconds = time * 60;

        for (UUID player : team.getMembers()) {
            teamShadowMutes.put(player, team.getName());
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_CREATED, ImmutableMap.of(
                "shadowMute", "true",
                "mutedById", sender.getUniqueId(),
                "mutedByName", sender.getName(),
                "duration", time
        ));

        new BukkitRunnable() {

            public void run() {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                        "shadowMute", "true"
                ));

                teamShadowMutes.entrySet().removeIf(mute -> mute.getValue().equalsIgnoreCase(team.getName()));
            }

        }.runTaskLater(Foxtrot.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.YELLOW + "Shadow muted the team " + team.getName() + ChatColor.GRAY + " for " + TimeUtils.formatIntoMMSS(timeSeconds) + ".");
    }

}
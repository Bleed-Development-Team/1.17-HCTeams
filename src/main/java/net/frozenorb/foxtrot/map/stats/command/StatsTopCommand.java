package net.frozenorb.foxtrot.map.stats.command;

import lombok.Getter;
import me.vaperion.blade.annotation.*;
import me.vaperion.blade.argument.BladeArgument;
import me.vaperion.blade.argument.BladeProvider;
import me.vaperion.blade.context.BladeContext;
import me.vaperion.blade.exception.BladeExitMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.stats.StatsEntry;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class StatsTopCommand {

    @Command(value = {"statstop", "leaderboards"})
    public static void statstop(@Sender CommandSender sender, @Optional(value = "kills") StatsObjective objective) {
        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
        sender.sendMessage(ChatColor.YELLOW + "Leaderboards for: " + ChatColor.RED + objective.getName());
        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));

        int index = 0;

        for (Map.Entry<StatsEntry, String> entry : Foxtrot.getInstance().getMapHandler().getStatsHandler().getLeaderboards(objective, 10).entrySet()) {
            index++;
            sender.sendMessage((index == 1 ? ChatColor.RED + "1 " : ChatColor.YELLOW.toString() + index + " ") + ChatColor.YELLOW + UUIDUtils.name(entry.getKey().getOwner()) + ": " + ChatColor.RED + entry.getValue());
        }

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    }

    @Getter
    public enum StatsObjective {

        KILLS("Kills", "k"),
        DEATHS("Deaths", "d"),
        KD("KD", "kdr"),
        HIGHEST_KILLSTREAK("Highest Killstreak", "killstreak", "highestkillstreak", "ks", "highestks", "hks");

        private String name;
        private String[] aliases;

        StatsObjective(String name, String... aliases) {
            this.name = name;
            this.aliases = aliases;
        }
    }

    public static class StatsObjectiveProvider implements BladeProvider<StatsObjective>{

        @Override
        public @Nullable StatsObjective provide(@NotNull BladeContext context, @NotNull BladeArgument argument) throws BladeExitMessage {

            return switch (argument.getString()) {
                case "d", "deaths" -> StatsObjective.DEATHS;
                case "kdr", "kd" -> StatsObjective.KD;
                default -> StatsObjective.KILLS;
            };
        }
    }

}

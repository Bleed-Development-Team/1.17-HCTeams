package net.frozenorb.foxtrot.map.stats.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.contexts.ContextResolver;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.stats.StatsEntry;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
@CommandAlias("statstop|leaderboards")
public class StatsTopCommand extends BaseCommand {

    @Default
    public static void statstop(Player sender, @Optional StatsObjective objective) {
        if (objective == null){
            objective = StatsObjective.KILLS;
        }

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

    public static class StatsObjectiveProvider implements ContextResolver<StatsObjective, BukkitCommandExecutionContext> {


        @Override
        public StatsObjective getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
            return switch (arg.popFirstArg()) {
                case "d", "deaths" -> StatsObjective.DEATHS;
                case "kdr", "kd" -> StatsObjective.KD;
                default -> StatsObjective.KILLS;
            };
        }
    }

}

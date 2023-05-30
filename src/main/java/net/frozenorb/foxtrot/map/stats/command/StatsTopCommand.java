package net.frozenorb.foxtrot.map.stats.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.contexts.ContextResolver;
import lombok.Getter;
import net.frozenorb.foxtrot.map.stats.command.menu.LeaderboardMenu;
import org.bukkit.entity.Player;

@CommandAlias("statstop|leaderboards")
public class StatsTopCommand extends BaseCommand {

    @Default
    public static void statstop(Player sender) {
        new LeaderboardMenu(sender.getPlayer()).updateMenu();
        
        /*

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
        sender.sendMessage(ChatColor.YELLOW + "Leaderboards for: " + ChatColor.RED + objective.getName());
        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));

        int index = 0;

        for (Map.Entry<StatsEntry, String> entry : Foxtrot.getInstance().getMapHandler().getStatsHandler().getLeaderboards(objective, 10).entrySet()) {
            index++;
            sender.sendMessage((index == 1 ? ChatColor.RED + "1 " : ChatColor.YELLOW.toString() + index + " ") + ChatColor.YELLOW + UUIDUtils.name(entry.getKey().getOwner()) + ": " + ChatColor.RED + entry.getValue());
        }

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));\
        
         */
    }

    @Getter
    public enum StatsObjective {

        KILLS("&6&lKills", "&e"),
        DEATHS("&c&lDeaths", "&4"),
        TEAM("&a&lTeam", "&2"),
        HIGHEST_KILLSTREAK("&d&lHighest Killstreak", "&5");


        private String name;
        private String color;

        StatsObjective(String name, String color) {
            this.name = name;
            this.color = color;
        }
    }

    public static class StatsObjectiveProvider implements ContextResolver<StatsObjective, BukkitCommandExecutionContext> {


        @Override
        public StatsObjective getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
            return switch (arg.popFirstArg()) {
                case "d", "deaths" -> StatsObjective.DEATHS;
                case "team", "t" -> StatsObjective.TEAM;
                default -> StatsObjective.KILLS;
            };
        }
    }

}

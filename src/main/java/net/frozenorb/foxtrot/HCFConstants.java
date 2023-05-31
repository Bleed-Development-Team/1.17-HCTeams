package net.frozenorb.foxtrot;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamCommands;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class HCFConstants {

    public static String teamChatFormat(Player player, String message) {
        return (ChatColor.DARK_AQUA + "(Team) " + player.getName() + ": " + ChatColor.YELLOW + message);
    }

    public static String officerChatFormat(Player player, String message) {
        return (ChatColor.LIGHT_PURPLE + "(Officer) " + player.getName() + ": " + ChatColor.YELLOW + message);
    }

    public static String teamChatSpyFormat(Team team, Player player, String message) {
        return (ChatColor.GOLD + "[" + ChatColor.DARK_AQUA + "TC: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + ChatColor.DARK_AQUA + player.getName() + ": " + message);
    }

    public static String allyChatFormat(Player player, String message) {
        return (Team.ALLY_COLOR + "(Ally) " + player.getName() + ": " + ChatColor.YELLOW + message);
    }

    public static String allyChatSpyFormat(Team team, Player player, String message) {
        return (ChatColor.GOLD + "[" + Team.ALLY_COLOR + "AC: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + Team.ALLY_COLOR + player.getName() + ": " + message);
    }

    public static String publicChatFormat(Team team, String rankPrefix, String customPrefixString) {
        String starting = "";

        if (team != null) {
            if (rankPrefix.toLowerCase().contains("famous") || rankPrefix.toLowerCase().contains("youtube")) {
                rankPrefix = "";
            }

            starting = ChatColor.GOLD + "[" + HCF.getInstance().getServerHandler().getDefaultRelationColor() + team.getName() + ChatColor.GOLD + "] ";
        }
        return customPrefixString + starting + ChatColor.WHITE + CC.translate(rankPrefix) + "%s" + ChatColor.WHITE + ": %s";
    }

    public static String publicChatFormatTwoPointOhBaby(Player player, Team team, String rankPrefix, String customPrefixString, String customColorString) {
        String starting = "";

        if (team != null) {
            if (TeamCommands.getSortedTeams().entrySet().iterator().next().getKey().equals(team)) {
                starting = ChatColor.GOLD + "[" +
                        (!customColorString.equals("") ? HCF.getInstance().getServerHandler().getDefaultRelationColor() : customColorString)
                        + team.getName() + ChatColor.GOLD  + "] ";
                return customPrefixString + starting + CC.translate(rankPrefix) + "%s" + ChatColor.WHITE + ": %s";
            }

            starting = ChatColor.GOLD + "[" +
                    (!customColorString.equals("") ? HCF.getInstance().getServerHandler().getDefaultRelationColor() : customColorString)
                    + team.getName() + ChatColor.GOLD  + "] ";

        }
        return customPrefixString + CC.translate(starting) + ChatColor.WHITE + CC.translate(rankPrefix) + "%s"  + ChatColor.WHITE + ": %s";
    }

    public static String getPrefix() {
        return CC.translate("&7[&b&lCrates&7]");
    }
}
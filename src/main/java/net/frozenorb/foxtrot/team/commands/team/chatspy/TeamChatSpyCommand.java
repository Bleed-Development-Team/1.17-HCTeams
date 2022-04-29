package net.frozenorb.foxtrot.team.commands.team.chatspy;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("chatspy")
@CommandPermission("foxtrot.team.chatspy")
public class TeamChatSpyCommand extends BaseCommand {

    @Subcommand("add")
    @CommandCompletion("@team")
    public static void teamChatSpyAdd(Player sender, @Name("team") Team team) {
        if (Foxtrot.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()).contains(team.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are already spying on " + team.getName() + ".");
            return;
        }

        List<ObjectId> teams = new ArrayList<>(Foxtrot.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()));

        teams.add(team.getUniqueId());

        Foxtrot.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), teams);
        sender.sendMessage(ChatColor.GREEN + "You are now spying on the chat of " + ChatColor.YELLOW + team.getName() + ChatColor.GREEN + ".");
    }
    @Subcommand("clear")
    @Description("Clears your chat Spy list")
    public static void teamChatSpyClear(Player sender) {
        Foxtrot.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), new ArrayList<ObjectId>());
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on any teams.");
    }

    @Subcommand("del")
    @Description("Stops spying on a team")
    @CommandCompletion("@team")
    public static void teamChatSpyDel(Player sender, @Name("team") Team team) {
        if (!Foxtrot.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()).contains(team.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not spying on " + team.getName() + ".");
            return;
        }

        List<ObjectId> teams = new ArrayList<>(Foxtrot.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()));

        teams.remove(team.getUniqueId());

        Foxtrot.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), teams);
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on the chat of " + ChatColor.YELLOW + team.getName() + ChatColor.GREEN + ".");
    }
    @Subcommand("list")
    @Description("Lists the teams you are spying on")
    public static void teamChatSpyList(Player sender) {
        StringBuilder stringBuilder = new StringBuilder();

        for (ObjectId team : Foxtrot.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId())) {
            Team teamObj = Foxtrot.getInstance().getTeamHandler().getTeam(team);

            if (teamObj != null) {
                stringBuilder.append(ChatColor.YELLOW).append(teamObj.getName()).append(ChatColor.GOLD).append(", ");
            }
        }

        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "You are currently spying on the team chat of: " + stringBuilder.toString());
    }
    @Default
    @HelpCommand
    @Description("Toggles chat spying on or off")
    public static void teamChatSpy(Player sender) {
        sender.sendMessage(ChatColor.RED + "/chatspy list - views teams who you are spying on");
        sender.sendMessage(ChatColor.RED + "/chatspy add - starts spying on a team");
        sender.sendMessage(ChatColor.RED + "/chatspy del - stops spying on a team");
        sender.sendMessage(ChatColor.RED + "/chatspy clear - stops spying on all teams");
    }

}
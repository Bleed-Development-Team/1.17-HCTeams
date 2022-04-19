package net.frozenorb.foxtrot.team.commands.team.chatspy;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatSpyCommand {

    @Command(value={ "team chatspy", "t chatspy", "f chatspy", "faction chatspy", "fac chatspy" })
    @Permission(value = "foxtrot.chatspy")
    public static void teamChatSpy(Player sender) {
        sender.sendMessage(ChatColor.RED + "/team chatspy list - views teams who you are spying on");
        sender.sendMessage(ChatColor.RED + "/team chatspy add - starts spying on a team");
        sender.sendMessage(ChatColor.RED + "/team chatspy del - stops spying on a team");
        sender.sendMessage(ChatColor.RED + "/team chatspy clear - stops spying on all teams");
    }

}
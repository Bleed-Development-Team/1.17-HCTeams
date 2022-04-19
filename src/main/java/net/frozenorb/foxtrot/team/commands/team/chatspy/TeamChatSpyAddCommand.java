package net.frozenorb.foxtrot.team.commands.team.chatspy;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamChatSpyAddCommand {

    @Command(value ={ "team chatspy add", "t chatspy add", "f chatspy add", "faction chatspy add", "fac chatspy add" })
    @Permission(value = "foxtrot.chatspy")
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

}
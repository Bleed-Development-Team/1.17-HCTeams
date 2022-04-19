package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamTPCommand {

    @Command(value={ "team tp", "t tp", "f tp", "faction tp", "fac tp" })
    @Permission(value = "foxtrot.factiontp")
    public static void teamTP(@Sender Player sender, @Name("team") Team team) {
        if (team.getHQ() != null) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
            sender.teleport(team.getHQ());
        } else if (team.getClaims().size() != 0) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s claim.");
            sender.teleport(team.getClaims().get(0).getMaximumPoint().add(0, 100, 0));
        } else {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " doesn't have a HQ or any claims.");
        }
    }

}

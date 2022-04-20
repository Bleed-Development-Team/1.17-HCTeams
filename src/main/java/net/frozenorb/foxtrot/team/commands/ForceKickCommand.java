package net.frozenorb.foxtrot.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

@CommandAlias("forcekick")
@CommandPermission("foxtrot.team.forcekick")
public class ForceKickCommand extends BaseCommand {

    @Default
    @Description("Force a player to leave your team")
    public static void forceKick(Player sender, Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not on a team!");
            return;
        }

        if (team.getMembers().size() == 1) {
            sender.sendMessage(ChatColor.RED + player.getName() + "'s team has one member. Please use /forcedisband to perform this action.");
            return;
        }

        if (team.getFocusedTeam().getHQ() != null){
            LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                    team.getFocusedTeam().getName() + "'s HQ",
                    team.getFocusedTeam().getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        if (team.getTeamRally() != null){
            LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                    "Team Rally",
                    team.getTeamRally(),
                    Color.AQUA.hashCode(),
                    true
            ));
        }

        if (team.getHQ() != null){
            LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        team.removeMember(player.getUniqueId());
        Foxtrot.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);

        Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.sendMessage(ChatColor.RED + "You were kicked from your team by a staff member.");
        }

        sender.sendMessage(ChatColor.YELLOW + "Force kicked " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + " from their team, " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
    }

}
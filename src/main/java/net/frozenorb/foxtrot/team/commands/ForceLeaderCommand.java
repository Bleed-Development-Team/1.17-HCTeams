package net.frozenorb.foxtrot.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("forceleader")
@CommandPermission("foxtrot.team.forceleader")
public class ForceLeaderCommand extends BaseCommand {


    @Default
    @Description("Force a player to be the leader of a team")
    public static void forceLeader(Player sender, Player player) {
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (playerTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "That player is not on a team.");
            return;
        }

        Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());

        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.sendMessage(ChatColor.YELLOW + "A staff member has made you leader of §b" + playerTeam.getName() + "§e.");
        }

        playerTeam.setOwner(player.getUniqueId());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + " is now the owner of " + ChatColor.LIGHT_PURPLE + playerTeam.getName() + ChatColor.YELLOW + ".");
    }

}
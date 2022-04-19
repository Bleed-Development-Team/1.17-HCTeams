package net.frozenorb.foxtrot.team.commands;

import me.vaperion.blade.annotation.*;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceLeaderCommand {

    @Command(value ={ "ForceLeader" })
    @Permission(value = "foxtrot.forceleader")
    public static void forceLeader(@Sender Player sender, @Optional("self") Player player) {
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
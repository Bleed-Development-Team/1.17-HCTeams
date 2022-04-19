package net.frozenorb.foxtrot.team.commands.team.subclaim;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSubclaimInfoCommand {

    @Command(value ={ "team subclaim info", "t subclaim info", "f subclaim info", "faction subclaim info", "fac subclaim info", "team sub info", "t sub info", "f sub info", "faction sub info", "fac sub info" })
    public static void teamSubclaimInfo(@Sender Player sender, @Optional("subclaim") Subclaim subclaim) {
        sender.sendMessage(ChatColor.BLUE + subclaim.getName() + ChatColor.YELLOW + " Subclaim Info");
        sender.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.GRAY + "Pos1. " + ChatColor.WHITE + subclaim.getLoc1().getBlockX() + "," + subclaim.getLoc1().getBlockY() + "," + subclaim.getLoc1().getBlockZ() + ChatColor.GRAY + " Pos2. " + ChatColor.WHITE + subclaim.getLoc2().getBlockX() + "," + subclaim.getLoc2().getBlockY() + "," + subclaim.getLoc2().getBlockZ());
        sender.sendMessage(ChatColor.YELLOW + "Members: " + ChatColor.WHITE + subclaim.getMembers().toString().replace("[", "").replace("]", ""));
    }

}
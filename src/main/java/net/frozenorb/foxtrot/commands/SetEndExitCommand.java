package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.listener.EndListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("setendexit")
@CommandPermission("op")
public class SetEndExitCommand extends BaseCommand {


    @Default
    public static void setendexit(Player sender) {
        Location previous = EndListener.getEndReturn();
        EndListener.setEndReturn(sender.getLocation());
        Location current = EndListener.getEndReturn();

        sender.sendMessage(
                ChatColor.GREEN + "End exit (" + ChatColor.WHITE + previous.getBlockX() + ":" + previous.getBlockY() + ":" + previous.getBlockZ() + ChatColor.GREEN + " -> " +
                        ChatColor.WHITE + current.getBlockX() + ":" + current.getBlockY() + ":" + current.getBlockZ() + ChatColor.GREEN + ")"
        );

        EndListener.saveEndReturn();
    }

}
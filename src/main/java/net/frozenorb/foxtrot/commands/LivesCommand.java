package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


@CommandAlias("lives")
public class LivesCommand extends BaseCommand {

    @Default
    public static void lives(Player commandSender) {
        
        if (commandSender == null) {
            commandSender.sendMessage(ChatColor.RED + "Bad console.");
            return;
        }

        Player sender = (Player) commandSender;
        
        int shared = Foxtrot.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());
        int soulbound = Foxtrot.getInstance().getSoulboundLivesMap().getLives(sender.getUniqueId());
        sender.sendMessage(ChatColor.YELLOW + "Lives are used to revive you instantly upon death. You can purchase more lives at: " + ChatColor.RED + "http://" + Foxtrot.getInstance().getServerHandler().getNetworkWebsite() + "/store");
        sender.sendMessage(ChatColor.YELLOW + "Friend Lives: " + ChatColor.RED + shared);
        sender.sendMessage(ChatColor.YELLOW + "Soulbound Lives: " + ChatColor.RED + soulbound);
        sender.sendMessage(ChatColor.RED + "You cannot revive other players with soulbound lives.");
    }
}

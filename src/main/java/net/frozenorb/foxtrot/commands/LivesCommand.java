package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.HelpCommand;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


@CommandAlias("lives")
public class LivesCommand extends BaseCommand {

    @Default
    @HelpCommand
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

    @Subcommand("add")
    @CommandPermission("lives.add")
    public void onAddCommand(Player sender, @Flags("other") Player target, int amount) {
        Foxtrot.getInstance().getFriendLivesMap().setLives(target.getUniqueId(), Foxtrot.getInstance().getFriendLivesMap().getLives(target.getUniqueId()) + amount);
        sender.sendMessage(ChatColor.YELLOW + "Added " + amount + " lives to " + target.getName() + "'s account.");
    }
}

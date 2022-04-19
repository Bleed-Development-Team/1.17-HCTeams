package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.ServerHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("logout")
public class LogoutCommand extends BaseCommand {

    @Default
    public static void logout(Player sender) {
        if (sender.hasMetadata("frozen")) {
            sender.sendMessage(ChatColor.RED + "You can't log out while you're frozen!");
            return;
        }

        if(ServerHandler.getTasks().containsKey(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You are already logging out.");
            return; // dont potato and let them spam logouts
        }

        Foxtrot.getInstance().getServerHandler().startLogoutSequence(sender);
    }

}
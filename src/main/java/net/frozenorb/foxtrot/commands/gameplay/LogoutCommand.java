package net.frozenorb.foxtrot.commands.gameplay;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.HCF;
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

        HCF.getInstance().getServerHandler().startLogoutSequence(sender);
    }

}
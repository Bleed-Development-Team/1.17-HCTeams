package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("crowbar")
@CommandPermission("op")
public class CrowbarCommand extends BaseCommand {


    @Default
    public static void crowbar(Player sender, @Optional Player target) {
        if (target != null) {
            target.getInventory().addItem(InventoryUtils.CROWBAR);
            target.sendMessage(ChatColor.YELLOW + "You received a crowbar from " + sender.getName() + ".");
            sender.sendMessage(ChatColor.YELLOW + "You gave a crowbar to " + target.getName() + ".");
        }
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        sender.getInventory().setItemInMainHand(InventoryUtils.CROWBAR);
        sender.sendMessage(ChatColor.YELLOW + "Gave you a crowbar.");
    }


}
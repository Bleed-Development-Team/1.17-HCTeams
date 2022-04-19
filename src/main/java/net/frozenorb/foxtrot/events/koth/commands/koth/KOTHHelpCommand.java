package net.frozenorb.foxtrot.events.koth.commands.koth;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHHelpCommand {

    @Command(value={ "KOTH Help" })
    @Permission(value = "foxtrot.koth")
    public static void kothHelp(@Sender Player sender) {
        sender.sendMessage(ChatColor.RED + "/koth list - Lists KOTHs");
        sender.sendMessage(ChatColor.RED + "/koth activate <name> - Activates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth deactivate <name> - Deactivates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth loc <name> - Set a KOTH's cap location");
        sender.sendMessage(ChatColor.RED + "/koth time <name> <time> - Sets a KOTH's cap time");
        sender.sendMessage(ChatColor.RED + "/koth dist <name> <distance> - Sets a KOTH's cap distance");
        sender.sendMessage(ChatColor.RED + "/koth tp <name> - TPs to a KOTH's");
        sender.sendMessage(ChatColor.RED + "/koth create <name> - Creates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth delete <name> - Deletes a KOTH");
    }

}
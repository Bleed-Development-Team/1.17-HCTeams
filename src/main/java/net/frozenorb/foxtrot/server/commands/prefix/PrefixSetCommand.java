package net.frozenorb.foxtrot.server.commands.prefix;

import me.vaperion.blade.annotation.*;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PrefixSetCommand {

    @Command(value={ "prefix set" })
    @Permission(value = "op")
    public static void prefixSet(@Sender CommandSender sender, @Name("player") UUID player, @Name("prefix")@Combined String prefix) {
        if (!prefix.equals("null")) {
            Foxtrot.getInstance().getChatHandler().setCustomPrefix(player, ChatColor.translateAlternateColorCodes('&', prefix));
        } else {
            Foxtrot.getInstance().getChatHandler().setCustomPrefix(player, null);
        }

        sender.sendMessage(ChatColor.YELLOW + "Prefix updated.");
    }

}
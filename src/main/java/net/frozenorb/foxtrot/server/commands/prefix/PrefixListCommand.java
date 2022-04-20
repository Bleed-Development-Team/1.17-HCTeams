package net.frozenorb.foxtrot.server.commands.prefix;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@CommandAlias("prefix")
@CommandPermission("op")
public class PrefixListCommand extends BaseCommand {


    @Subcommand("list")
    public static void prefixList(Player sender) {
        for (Map.Entry<UUID, String> prefixEntry : Foxtrot.getInstance().getChatHandler().getAllCustomPrefixes()) {
            sender.sendMessage(ChatColor.YELLOW + FrozenUUIDCache.name(prefixEntry.getKey()) + ": " + ChatColor.RESET + prefixEntry.getValue());

        }
    }
    @Subcommand("set")
    public static void prefixSet(CommandSender sender, UUID player, String prefix) {
        if (!prefix.equals("null")) {
            Foxtrot.getInstance().getChatHandler().setCustomPrefix(player, ChatColor.translateAlternateColorCodes('&', prefix));
        } else {
            Foxtrot.getInstance().getChatHandler().setCustomPrefix(player, null);
        }

        sender.sendMessage(ChatColor.YELLOW + "Prefix updated.");
    }

}
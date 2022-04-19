package net.frozenorb.foxtrot.server.commands.prefix;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class PrefixListCommand {

    @Command(value={ "prefix list"})
    @Permission(value = "op")
    public static void prefixList(@Sender Player sender) {
        for (Map.Entry<UUID, String> prefixEntry : Foxtrot.getInstance().getChatHandler().getAllCustomPrefixes()) {
            sender.sendMessage(ChatColor.YELLOW + FrozenUUIDCache.name(prefixEntry.getKey()) + ": " + ChatColor.RESET + prefixEntry.getValue());
        }
    }

}
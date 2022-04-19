package net.frozenorb.foxtrot.server.commands.betrayer;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.Betrayer;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BetrayerRemoveCommand {

    @Command(value = {"betrayer remove"})
    @Permission(value = "op")
    public static void betrayerRemove(@Sender Player sender, @Name(value = "player") UUID player) {
        Betrayer betrayer = Foxtrot.getInstance().getServerHandler().getBetrayer(player);
        if (betrayer != null) {
            Foxtrot.getInstance().getServerHandler().getBetrayers().remove(betrayer);
            Foxtrot.getInstance().getServerHandler().save();

            sender.sendMessage(ChatColor.GREEN + "Removed " + UUIDUtils.name(player) + "'s betrayer tag.");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " isn't a betrayer.");
        }
    }

}
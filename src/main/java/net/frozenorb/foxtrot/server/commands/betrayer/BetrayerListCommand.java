package net.frozenorb.foxtrot.server.commands.betrayer;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.Betrayer;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BetrayerListCommand {

    @Command(value = {"betrayer list", "betrayers"})
    public static void betrayerList(@Sender Player sender) {
        StringBuilder betrayers = new StringBuilder();

        for (Betrayer betrayer : Foxtrot.getInstance().getServerHandler().getBetrayers()) {
            betrayers.append(ChatColor.GRAY).append(UUIDUtils.name(betrayer.getUuid())).append(ChatColor.GOLD).append(", ");
        }

        if (betrayers.length() > 2) {
            betrayers.setLength(betrayers.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "Betrayers: " + betrayers.toString());
    }

}
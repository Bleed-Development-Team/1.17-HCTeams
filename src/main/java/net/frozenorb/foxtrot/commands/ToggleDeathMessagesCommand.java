package net.frozenorb.foxtrot.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleDeathMessagesCommand {

    @Command(value = {"toggledeathmessages", "tdm"})
    public static void toggledeathmessages(@Sender Player sender) {
        boolean val = !Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see Death Messages!");
        Foxtrot.getInstance().getToggleDeathMessageMap().setDeathMessagesEnabled(sender.getUniqueId(), val);
    }

}

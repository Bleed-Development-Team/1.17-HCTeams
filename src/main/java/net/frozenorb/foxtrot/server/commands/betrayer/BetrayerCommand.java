package net.frozenorb.foxtrot.server.commands.betrayer;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;

public class BetrayerCommand {

    @Command({"betrayer"})
    @Permission("op")
    public static void betrayer(@Sender Player sender) {
        String[] msges = {
                "§c/betrayer list - Shows all betrayers.",
                "§c/betrayer add <player> <reason> - Add a betrayer for a reason.",
                "§c/betrayer remove <player> - Remove a betrayer."};

        sender.sendMessage(msges);
    }


}
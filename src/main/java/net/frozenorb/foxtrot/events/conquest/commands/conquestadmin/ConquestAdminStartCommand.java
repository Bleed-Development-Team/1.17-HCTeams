package net.frozenorb.foxtrot.events.conquest.commands.conquestadmin;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.conquest.game.ConquestGame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConquestAdminStartCommand {

    @Command(value ={ "conquestadmin start" })
    @Permission(value = "op")
    public static void conquestAdminStart(@Sender CommandSender sender) {
        ConquestGame game = Foxtrot.getInstance().getConquestHandler().getGame();

        if (game != null) {
            sender.sendMessage(ChatColor.RED + "Conquest is already active.");
            return;
        }

        new ConquestGame();
    }

}
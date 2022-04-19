package net.frozenorb.foxtrot.team.commands.pvp;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class PvPCreateCommand {

    @Command(value ={ "pvp create", "timer create", "pvptimer create", "pvp add", "pvp give" })
    @Permission(value = "worldedit.*")
    public static void pvpCreate(@Sender Player sender, @Optional("self") Player player) {
        Foxtrot.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(30));
        player.sendMessage(ChatColor.YELLOW + "You have 30 minutes of PVP Timer!");

        if (sender != player) {
            sender.sendMessage(ChatColor.YELLOW + "Gave 30 minutes of PVP Timer to " + player.getName() + ".");
        }
    }

}
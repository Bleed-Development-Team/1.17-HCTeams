package net.frozenorb.foxtrot.server.commands.betrayer;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.Betrayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static net.frozenorb.foxtrot.uuid.FrozenUUIDCache.name;
import static org.bukkit.ChatColor.*;

public class BetrayerInfoCommand {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yy HH:mm:ss z");

    @Command(value = {"betrayer info", "bitch info"})
    public static void betrayerList(@Sender Player sender, @Name(value = "player") UUID player) {
        Betrayer betrayer = Foxtrot.getInstance().getServerHandler().getBetrayer(player);

        if (betrayer != null) {
            sender.sendMessage(GOLD + "=====" + WHITE + " Betrayer Information " + GOLD + "=====");
            sender.sendMessage(GOLD + "Betrayer: " + LIGHT_PURPLE + name(betrayer.getUuid()) + GOLD + " Added by: " + LIGHT_PURPLE + name(betrayer.getAddedBy()));
            sender.sendMessage(GOLD + "When: " + LIGHT_PURPLE + sdf.format(new Date(betrayer.getTime())) + GOLD + " Why: " + LIGHT_PURPLE + betrayer.getReason());
        } else {
            sender.sendMessage(RED + "Could not find betrayer info for " + YELLOW + name(player) + RED + "!");
        }
    }
}

package net.frozenorb.foxtrot.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.maps.PlaytimeMap;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaytimeCommand {

    @Command(value ={ "Playtime", "PTime" })
    public static void playtime(@Sender Player sender, @Optional("self") OfflinePlayer player) {
        OfflinePlayer target = null;
        if (player == null){
            target = sender;
        } else {
            target = player;
        }

        PlaytimeMap playtime = Foxtrot.getInstance().getPlaytimeMap();
        int playtimeTime = (int) playtime.getPlaytime(target.getUniqueId());
        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(target.getUniqueId());

        if (bukkitPlayer != null && sender.canSee(bukkitPlayer)) {
            playtimeTime += playtime.getCurrentSession(bukkitPlayer.getUniqueId()) / 1000;
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + "'s total playtime is " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(playtimeTime) + ChatColor.YELLOW + ".");
    }

}
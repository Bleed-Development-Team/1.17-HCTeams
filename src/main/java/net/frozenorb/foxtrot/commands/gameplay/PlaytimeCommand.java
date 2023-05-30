package net.frozenorb.foxtrot.commands.gameplay;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.persist.maps.PlaytimeMap;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
@CommandAlias("Playtime|PTime")
public class PlaytimeCommand extends BaseCommand {

    @Default
    public static void playtime(Player sender, @Optional OfflinePlayer player) {
        OfflinePlayer target = null;
        if (player == null){
            target = sender;
        } else {
            target = player;
        }

        PlaytimeMap playtime = HCF.getInstance().getPlaytimeMap();
        int playtimeTime = (int) playtime.getPlaytime(target.getUniqueId());
        Player bukkitPlayer = HCF.getInstance().getServer().getPlayer(target.getUniqueId());

        if (bukkitPlayer != null && sender.canSee(bukkitPlayer)) {
            playtimeTime += playtime.getCurrentSession(bukkitPlayer.getUniqueId()) / 1000;
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + "'s total playtime is " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString(playtimeTime) + ChatColor.YELLOW + ".");
    }

}
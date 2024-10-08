package net.frozenorb.foxtrot.map.stats.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

@CommandAlias("stats")
public class StatsCommand extends BaseCommand {

    @Default
    public static void stats(Player sender, @Optional OfflinePlayer player) {
        UUID uuid = player == null ? sender.getUniqueId() : player.getUniqueId();

        if (player != null && !player.hasPlayedBefore()){
            sender.sendMessage(CC.translate("&cThat player hasn't played before."));
            return;
        }


        int kills = HCF.getInstance().getMapHandler().getStatsHandler().getStats(uuid).getKills();
        int deaths = HCF.getInstance().getMapHandler().getStatsHandler().getStats(uuid).getDeaths();

        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 36));
        sender.sendMessage(ChatColor.GOLD + (player == null ? sender.getName() : player.getName()) + ChatColor.WHITE + "'s  Statistics" + ChatColor.GRAY + ":");
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 36));

        sender.sendMessage(CC.translate(" &f» &6Kills&7: &f" + kills));
        sender.sendMessage(CC.translate(" &f» &6Deaths&7: &f" + deaths));
        sender.sendMessage(CC.translate(" &f» &6Balance&7: &f$" + NumberFormat.getNumberInstance(Locale.US).format(EconomyHandler.getBalance(uuid))));
        sender.sendMessage(CC.translate(" &f» &6KDR&7: &f" + (deaths == 0 ? "Infinity" : Team.DTR_FORMAT.format((double) kills / (double) deaths))));
        sender.sendMessage(CC.translate(" &f» &6Raidable Teams&7 &f" + HCF.getInstance().getRaidableTeamsMap().getRaidableTeams(uuid)));
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 36));
    }


}

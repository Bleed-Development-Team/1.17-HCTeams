package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("lff|lookingforafaction")
public class LFFCommand extends BaseCommand {

    @Default
    public static void lff(Player sender) {
        if (Cooldown.isOnCooldown("lff", sender)) {
            sender.sendMessage("§cYou must wait §e" + Cooldown.getCooldownString(sender, "lff") + " §cseconds before using this command again.");
            return;
        }
        if (Foxtrot.getInstance().getTeamHandler().getTeam(sender) != null) {
            sender.sendMessage("§cYou are already in a team.");
            return;
        }
        int kills = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(sender.getUniqueId()).getKills();
        int deaths = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(sender.getUniqueId()).getDeaths();

        Bukkit.broadcastMessage(CC.translate("&c"));
        Bukkit.broadcastMessage(CC.translate("&b&l " + sender.getName() + " &fis looking for a faction:"));
        Bukkit.broadcastMessage(CC.translate("     &eKills&7: &f" + kills + " &7| &eDeaths&7: &f" + deaths + " &7| &eKDR&7: &f" + (deaths == 0 ? "Infinity" : Team.DTR_FORMAT.format((double) kills / (double) deaths))));
        Bukkit.broadcastMessage(CC.translate("&c"));
        Bukkit.broadcastMessage(CC.translate("&e Type /f invite " + sender.getName() + " to invite them to your faction."));
        Bukkit.broadcastMessage(CC.translate("&c"));
        Cooldown.addCooldown("lff", sender, 300);
    }
}
package net.frozenorb.foxtrot.map.stats.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.map.stats.command.StatsTopCommand.StatsObjective;
import org.bukkit.ChatColor;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

@CommandAlias("leaderboard")
@CommandPermission("op")
public class LeaderboardAddCommand extends BaseCommand {

    @Subcommand ("add")
    public static void leaderboardAdd(Player sender, String objectiveName, int place) {
        Block block = sender.getTargetBlock(null, 10);
        StatsObjective objective;
        
        try {
            objective = StatsObjective.valueOf(objectiveName);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Invalid objective!");
            return;
        }

        if (block == null || !(block.getState() instanceof Skull || block.getState() instanceof Sign)) {
            sender.sendMessage(ChatColor.RED + "You must be looking at a head or a sign.");
            return;
        }

        if (block.getState() instanceof Skull) {
            Skull skull = (Skull) block.getState();

            if (skull.getSkullType() != SkullType.PLAYER) {
                sender.sendMessage(ChatColor.RED + "That's not a player skull.");
                return;
            }

            HCF.getInstance().getMapHandler().getStatsHandler().getLeaderboardHeads().put(skull.getLocation(), place);
            HCF.getInstance().getMapHandler().getStatsHandler().getObjectives().put(skull.getLocation(), objective);
            HCF.getInstance().getMapHandler().getStatsHandler().updatePhysicalLeaderboards();
            sender.sendMessage(ChatColor.GREEN + "This skull will now display the number " + ChatColor.WHITE + place + ChatColor.GREEN + " player's head.");
        } else {
            Sign sign = (Sign) block.getState();

            HCF.getInstance().getMapHandler().getStatsHandler().getLeaderboardSigns().put(sign.getLocation(), place);
            HCF.getInstance().getMapHandler().getStatsHandler().getObjectives().put(sign.getLocation(), objective);
            HCF.getInstance().getMapHandler().getStatsHandler().updatePhysicalLeaderboards();
            sender.sendMessage(ChatColor.GREEN + "This sign will now display the number " + ChatColor.WHITE + place + ChatColor.GREEN + " player's stats.");
        }
        
        HCF.getInstance().getMapHandler().getStatsHandler().getObjectives().put(block.getLocation(), objective);
    }



}

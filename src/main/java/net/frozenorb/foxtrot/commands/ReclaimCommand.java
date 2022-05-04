package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import ltd.matrixstudios.alchemist.api.AlchemistAPI;
import ltd.matrixstudios.alchemist.models.ranks.Rank;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ReclaimCommand extends BaseCommand {

    @CommandAlias("reclaim")
    public static void reclaim(Player player){
        Rank rank = AlchemistAPI.INSTANCE.quickFindProfile(player.getUniqueId()).getCurrentRank();

        if (Foxtrot.getInstance().getReclaimMap().hasReclaimed(player.getUniqueId())){
            player.sendMessage(CC.translate("&cYou have already reclaimed this map."));
            return;
        }

        if (Foxtrot.getInstance().getConfig().contains("reclaim." + rank.getName())) {
            List<String> commands = Foxtrot.getInstance().getConfig().getStringList("reclaim." + rank.getName());

            for (String command : commands) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
            }

            Bukkit.broadcastMessage(CC.translate(AlchemistAPI.INSTANCE.getRankDisplay(player.getUniqueId())
            + " &7has reclaimed their &r" + rank.getColor() + rank.getName() + " &7rank using &f/reclaim&7."));

            Foxtrot.getInstance().getReclaimMap().setReclaimed(player.getUniqueId(), true);
        } else {
            player.sendMessage(CC.translate("&cIt appears there is no reclaim for your rank."));
        }
    }

    @CommandAlias("resetreclaim")
    @CommandPermission("foxtrot.admin")
    public static void reset(Player player, @Name("player") Player target){
        if (!Foxtrot.getInstance().getReclaimMap().hasReclaimed(target.getUniqueId())){
            player.sendMessage(CC.translate("&cThat player has not reclaimed."));
            return;
        }

        Foxtrot.getInstance().getReclaimMap().setReclaimed(target.getUniqueId(), false);

        player.sendMessage(CC.translate("&aYou have reset &r" + target.getDisplayName() + "&a's reclaim."));
        target.sendMessage(CC.translate("&aA staff member has reset your reclaim."));
    }
}

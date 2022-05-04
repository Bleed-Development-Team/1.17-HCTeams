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

public class RedeemCommand extends BaseCommand {

    @CommandAlias("redeem")
    public static void reclaim(Player player, @Name("creator") String creator) {
        if (Foxtrot.getInstance().getRedeemMap().hasRedeemed(player.getUniqueId())){
            player.sendMessage(CC.translate("&cYou have already redeemed this map."));
            return;
        }

        if (Foxtrot.getInstance().getConfig().contains("redeem." + creator)) {
            List<String> commands = Foxtrot.getInstance().getConfig().getStringList("redeem." + creator);

            for (String command : commands) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
            }

            Bukkit.broadcastMessage(CC.translate(AlchemistAPI.INSTANCE.getRankDisplay(player.getUniqueId())
            + " &7has redeemed the creator &r" + creator + " &7using &f/redeem&7."));

            Foxtrot.getInstance().getRedeemMap().setRedeemed(player.getUniqueId(), true);
        } else {
            player.sendMessage(CC.translate("&cNo creator with the name " + creator + " found."));
        }
    }

    @CommandAlias("resetredeem")
    @CommandPermission("foxtrot.admin")
    public static void reset(Player player, @Name("player") Player target){
        if (!Foxtrot.getInstance().getRedeemMap().hasRedeemed(target.getUniqueId())){
            player.sendMessage(CC.translate("&cThat player has not redeemed."));
            return;
        }

        Foxtrot.getInstance().getReclaimMap().setReclaimed(target.getUniqueId(), false);

        player.sendMessage(CC.translate("&aYou have reset &r" + target.getDisplayName() + "&a's redeem."));
        target.sendMessage(CC.translate("&aA staff member has reset your redeem."));
    }
}

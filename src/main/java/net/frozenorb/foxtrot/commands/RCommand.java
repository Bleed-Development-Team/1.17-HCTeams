package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import ltd.matrixstudios.alchemist.api.AlchemistAPI;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("r|respond")
public class RCommand extends BaseCommand {

    @Default
    public void onMultiChannelRCommand(Player player, String message) {
        if (!MsgCommand.messaged.containsKey(player.getUniqueId())) {
            player.sendMessage("§cYou have not messaged anyone yet!");
            return;
        }
        Player target = Bukkit.getPlayer(MsgCommand.messaged.get(player.getUniqueId()));
        if (!target.isOnline()) {
            player.sendMessage("§cThat player is no longer online!");
            return;
        }
        player.sendMessage(CC.translate("&7(To &r" + target.getDisplayName() + "&7) " + message));
        target.sendMessage(CC.translate("&7(From &r" + player.getDisplayName() + "&7) " + message));
        MsgCommand.messaged.remove(player.getUniqueId());
        MsgCommand.messaged.remove(target.getUniqueId());
        MsgCommand.messaged.put(player.getUniqueId(), target.getUniqueId());
        MsgCommand.messaged.put(target.getUniqueId(), player.getUniqueId());
    }
}

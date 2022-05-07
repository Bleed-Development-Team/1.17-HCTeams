package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@CommandAlias("message|msg|m")
public class MsgCommand extends BaseCommand {
    public static HashMap<UUID, UUID> messaged = new HashMap<>();

    @Default
    public void onMultiChannelMessageCommand(Player player, @Flags("other")Player target, String message) {
        if (!player.canSee(target)) {
            return;
        }
        player.sendMessage(CC.translate("&7(To &r" + target.getDisplayName() + "&7) " + message));
        target.sendMessage(CC.translate("&7(From &r" + player.getDisplayName() + "&7) " + message));
        messaged.remove(player.getUniqueId());
        messaged.remove(target.getUniqueId());
        messaged.put(player.getUniqueId(), target.getUniqueId());
        messaged.put(target.getUniqueId(), player.getUniqueId());
    }

}

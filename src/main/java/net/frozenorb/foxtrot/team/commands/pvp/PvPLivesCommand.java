package net.frozenorb.foxtrot.team.commands.pvp;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPLivesCommand {

    @Command(value ={ "pvp lives", "timer lives", "pvptimer lives" })
    public static void pvpLives(@Sender CommandSender sender, @Optional("self") Player fakePlayer) {
        UUID player = fakePlayer.getUniqueId();
        String name = UUIDUtils.name(player);

        sender.sendMessage(ChatColor.GOLD + name + "'s Soulbound Lives: " + ChatColor.WHITE + Foxtrot.getInstance().getSoulboundLivesMap().getLives(player));
        sender.sendMessage(ChatColor.GOLD + name + "'s Friend Lives: " + ChatColor.WHITE + Foxtrot.getInstance().getFriendLivesMap().getLives(player));
    }

}
package net.frozenorb.foxtrot.team.commands.pvp;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPSetLivesCommand {

    @Command(value ={ "pvp setlives", "timer setlives", "pvptimer setlives", "timer setlives", "pvp setlives" })
    @Permission(value = "foxtrot.setlives")
    public static void pvpSetLives(@Sender CommandSender sender, @Name("player") Player fakePlayer, @Name("life type") String lifeType, @Name("amount") int amount) {
        UUID player = fakePlayer.getUniqueId();
        if (lifeType.equalsIgnoreCase("soulbound")) {
            Foxtrot.getInstance().getSoulboundLivesMap().setLives(player, amount);
            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + "'s soulbound life count to " + amount + ".");
        } else if (lifeType.equalsIgnoreCase("friend")) {
            Foxtrot.getInstance().getFriendLivesMap().setLives(player, amount);
            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + "'s friend life count to " + amount + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid life type: Options are soulbound or friend.");
        }
    }

}
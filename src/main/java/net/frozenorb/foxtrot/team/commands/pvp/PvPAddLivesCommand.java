package net.frozenorb.foxtrot.team.commands.pvp;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPAddLivesCommand {

    @Command(value = {"pvp addlives", "addlives"})
    @Permission(value = "foxtrot.addlives")
    public static void pvpSetLives(@Sender CommandSender sender, @Name("player") Player fakePlayer, @Name("life type") String lifeType, @Name("amount") int amount) {
        UUID player = fakePlayer.getUniqueId();
        if (lifeType.equalsIgnoreCase("soulbound")) {
            Foxtrot.getInstance().getSoulboundLivesMap().setLives(player, Foxtrot.getInstance().getSoulboundLivesMap().getLives(player) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " " + amount + " soulbound lives.");

            Player bukkitPlayer = Bukkit.getPlayer(player);
            if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                String suffix = sender instanceof Player ? " from " + sender.getName() : "";
                bukkitPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " lives" + suffix);
            }

        } else if (lifeType.equalsIgnoreCase("friend")) {
            Foxtrot.getInstance().getFriendLivesMap().setLives(player, Foxtrot.getInstance().getFriendLivesMap().getLives(player) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " " + amount + " friend lives.");

            Player bukkitPlayer = Bukkit.getPlayer(player);
            if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                String suffix = sender instanceof Player ? " from " + sender.getName() : "";
                bukkitPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " lives" + suffix);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid life type: Options are soulbound or friend");
        }
    }

}

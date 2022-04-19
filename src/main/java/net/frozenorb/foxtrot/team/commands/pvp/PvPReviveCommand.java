package net.frozenorb.foxtrot.team.commands.pvp;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPReviveCommand {

    @Command(value = {"pvp revive", "timer revive", "pvp revive", "pvptimer revive", "timer revive", "pvptimer revive" })
    public static void pvpRevive(@Sender Player sender, @Name("player") OfflinePlayer fakePlayer) {
        UUID player = fakePlayer.getUniqueId();
        int friendLives = Foxtrot.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());

        if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
            sender.sendMessage(ChatColor.RED + "The server is in EOTW Mode: Lives cannot be used.");
            return;
        }

        if (friendLives <= 0) {
            sender.sendMessage(ChatColor.RED + "You have no lives which can be used to revive other players!");
            return;
        }

        if (!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player)) {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().getBetrayer(player) != null) {
            sender.sendMessage(ChatColor.RED + "Betrayers may not be revived!");
            return;
        }

        // Use a friend life.
        Foxtrot.getInstance().getFriendLivesMap().setLives(sender.getUniqueId(), friendLives - 1);
        sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " with a friend life!");


        Foxtrot.getInstance().getDeathbanMap().revive(player);
    }

}
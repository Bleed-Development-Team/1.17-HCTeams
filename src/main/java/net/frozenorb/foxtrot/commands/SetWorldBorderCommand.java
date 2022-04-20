package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.BorderListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandAlias("SetWorldBorder")
@CommandPermission("op")
public class SetWorldBorderCommand extends BaseCommand {

    @Default
    public static void setWorldBorder(Player sender, int border) {
        BorderListener.BORDER_SIZE = border;
        sender.sendMessage(ChatColor.GRAY + "The world border is now set to " + BorderListener.BORDER_SIZE + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                Foxtrot.getInstance().getMapHandler().saveBorder();
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}
package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandAlias("setnetherbuffer")
@CommandPermission("op")
public class SetNetherBufferCommand extends BaseCommand {

    @Default
    public static void setNetherBuffer(Player sender, int newBuffer) {
        Foxtrot.getInstance().getMapHandler().setNetherBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The nether buffer is now set to " + newBuffer + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                Foxtrot.getInstance().getMapHandler().saveNetherBuffer();
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}

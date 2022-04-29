package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandAlias("setworldbuffer")
@CommandPermission("op")
public class SetWorldBufferCommand extends BaseCommand {


    @Default
    public static void setWorldBuffer(Player sender, @Name("newBuffer") int newBuffer) {
        Foxtrot.getInstance().getMapHandler().setWorldBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The world buffer is now set to " + newBuffer + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                Foxtrot.getInstance().getMapHandler().saveWorldBuffer();
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}

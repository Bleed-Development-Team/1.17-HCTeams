package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@CommandAlias("spawn")
public class SpawnCommand extends BaseCommand {

    @Default
    public static void spawn(Player sender) {
        if (sender.hasPermission("foxtrot.spawn") && sender.getGameMode() == GameMode.CREATIVE) {
            sender.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
        } else {
            if (CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(sender.getUniqueId())) {
                sender.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
                return;
            }

            // Make this pretty.
            String serverName = Foxtrot.getInstance().getServerHandler().getServerName();

            sender.sendMessage(ChatColor.RED + serverName + " does not have a spawn command! You must walk there!");
            sender.sendMessage(ChatColor.RED + "Spawn is located at 0,0.");
        }
    }

}

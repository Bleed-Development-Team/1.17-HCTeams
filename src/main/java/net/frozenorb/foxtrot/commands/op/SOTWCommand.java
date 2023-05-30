package net.frozenorb.foxtrot.commands.op;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("sotw")
public class SOTWCommand extends BaseCommand {
    @Subcommand("enable")
    public static void sotwEnable(Player sender) {
        if (!CustomTimerCreateCommand.isSOTWTimer()) {
            sender.sendMessage(ChatColor.RED + "You can't /sotw enable when there is no SOTW timer...");
            return;
        }

        if (CustomTimerCreateCommand.sotwEnabled.add(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.GREEN + "Successfully disabled your SOTW timer.");
        } else {
            sender.sendMessage(ChatColor.RED + "Your SOTW timer was already disabled...");
        }
    }

    @Subcommand("spawn")
    public static void sotwSpawn(Player player){
        if (!CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "There is no SOTW timer!");
            return;
        }

        player.teleport(player.getWorld().getSpawnLocation());
    }


    @Subcommand("cancel")
    @CommandPermission("foxtrot.sotw")
    public static void sotwCancel(Player sender) {
        Long removed = CustomTimerCreateCommand.customTimers.remove("&a&lSOTW");
        if (removed != null && System.currentTimeMillis() < removed) {
            sender.sendMessage(ChatColor.GREEN + "Deactivated the SOTW timer.");
            return;
        }

        sender.sendMessage(ChatColor.RED + "SOTW timer is not active.");
    }

    @Subcommand("start")
    @CommandPermission("foxtrot.sotw")
    public static void sotwStart(Player sender, String time) {
        int seconds = TimeUtils.parseTime(time);
        if (seconds < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        CustomTimerCreateCommand.customTimers.put("&a&lSOTW", System.currentTimeMillis() + (seconds * 1000L));
        sender.sendMessage(ChatColor.GREEN + "Started the SOTW timer for " + time);

        CustomTimerCreateCommand.sotwEnabled.clear();
    }


    @Subcommand("extend")
    @CommandPermission("foxtrot.sotw")
    public static void sotwExtend(Player sender, String time) {
        int seconds;
        try {
            seconds = TimeUtils.parseTime(time);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        if (seconds < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        if (!CustomTimerCreateCommand.customTimers.containsKey("&a&lSOTW")) {
            sender.sendMessage(ChatColor.RED + "There is currently no active SOTW timer.");
            return;
        }

        CustomTimerCreateCommand.customTimers.put("&a&lSOTW", CustomTimerCreateCommand.customTimers.get("&a&lSOTW") + (seconds * 1000L));
        sender.sendMessage(ChatColor.GREEN + "Extended the SOTW timer by " + time);
    }
}

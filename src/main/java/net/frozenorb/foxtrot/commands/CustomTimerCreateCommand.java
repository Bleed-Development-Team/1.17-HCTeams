package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
@CommandAlias("customtimer")
@CommandPermission("foxtrot.customtimer")
public class CustomTimerCreateCommand extends BaseCommand {

    @Getter public static Map<String, Long> customTimers = new HashMap<>();
    public static Set<UUID> sotwEnabled = Sets.newHashSet();

    @Subcommand("create")
    public static void customTimerCreate(CommandSender sender, @Name("time") String time, @Name("title") String title) {
        int newTime = TimeUtils.parseTime(time);
        if (newTime == 0) {
            customTimers.remove(title);
        } else {
            customTimers.put(title, System.currentTimeMillis() + (newTime * 1000L));
        }
    }


    @Subcommand("delete")
    public static void customTimerDelete(Player sender, @Name("title") String title) {
        if (customTimers.containsKey(title)) {
            customTimers.remove(title);
        } else {
            sender.sendMessage("&cNo custom timer with the name " + title + " found.");
        }
    }


    public static boolean isSOTWTimer() {
        return customTimers.containsKey("&a&lSOTW");
    }

    public static boolean hasSOTWEnabled(UUID uuid) {
        return sotwEnabled.contains(uuid);
    }
}
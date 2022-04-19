package net.frozenorb.foxtrot.team.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StartDTRRegenCommand {

    @Command(value={ "startdtrregen" })
    @Permission(value = "foxtrot.startdtrregen")
    public static void startDTRRegen(@Sender Player sender, @Name("team") Team team) {
        team.setDTRCooldown(System.currentTimeMillis());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " is now regenerating DTR.");
    }

}
package net.frozenorb.foxtrot.team.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("startdtrregen")
@CommandPermission("foxtrot.team.startdtrregen")
public class StartDTRRegenCommand  extends BaseCommand {


    @Default
    @Description("Starts a team's DTR regeneration")
    public static void startDTRRegen(Player sender, @Name("team") Team team) {
        team.setDTRCooldown(System.currentTimeMillis());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " is now regenerating DTR.");
    }

}
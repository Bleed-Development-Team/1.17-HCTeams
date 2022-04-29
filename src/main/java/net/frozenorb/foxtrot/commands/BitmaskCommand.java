package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("bitmask|bitmasks")
@CommandPermission("op")
public class BitmaskCommand extends BaseCommand {


    @Subcommand("list")
    public void bitmaskList(Player sender) {
        for (DTRBitmask bitmaskType : DTRBitmask.values()) {
            sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
        }
    }


    @Subcommand("info")
    @CommandCompletion("@team")
    public static void bitmaskInfo(Player sender, @Name("team") Team team) {
        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Bitmask flags of " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW + ":");

        for (DTRBitmask bitmaskType : DTRBitmask.values()) {
            if (!team.hasDTRBitmask(bitmaskType)) {
                continue;
            }

            sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
        }

        sender.sendMessage(ChatColor.GOLD + "Raw DTR: " + ChatColor.YELLOW + team.getDTR());
    }


    @Subcommand("add")
    public static void bitmaskAdd(Player sender, @Name("team") Team team, @Name("bitmask")DTRBitmask bitmask) {
        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        if (team.hasDTRBitmask(bitmask)) {
            sender.sendMessage(ChatColor.RED + "This claim already has the bitmask value " + bitmask.getName() + ".");
            return;
        }

        int dtrInt = (int) team.getDTR();

        dtrInt += bitmask.getBitmask();

        team.setDTR(dtrInt);
        bitmaskInfo(sender, team);
    }


    @Subcommand("remove")
    public void bitmaskRemove(Player sender, @Name("team") Team team, @Name("bitmask") DTRBitmask bitmask) {
        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        if (!team.hasDTRBitmask(bitmask)) {
            sender.sendMessage(ChatColor.RED + "This claim doesn't have the bitmask value " + bitmask.getName() + ".");
            return;
        }

        int dtrInt = (int) team.getDTR();

        dtrInt -= bitmask.getBitmask();

        team.setDTR(dtrInt);
        bitmaskInfo(sender, team);
    }

}
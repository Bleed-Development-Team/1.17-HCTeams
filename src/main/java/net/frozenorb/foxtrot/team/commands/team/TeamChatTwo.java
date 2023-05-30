package net.frozenorb.foxtrot.team.commands.team;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatTwo extends BaseCommand {

    @CommandAlias("gc|pc")
    public static void gc(Player sender) {
        TeamChatCommand.setChat(sender, ChatMode.PUBLIC);
    }

    @CommandAlias("hq")
    public static void hq(Player sender){
        Team team = HCF.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.DARK_AQUA + "You are not on a team!");
            return;
        }

        if (team.getHQ() == null) {
            sender.sendMessage(ChatColor.RED + "HQ not set.");
            return;
        }

        if (HCF.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters during the End of the World!");
            return;
        }

        if (sender.hasMetadata("frozen")) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters while you're frozen!");
            return;
        }

        if (HCF.getInstance().getInDuelPredicate().test(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a duel!");
            return;
        }

        if (HCF.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Use /pvp enable to toggle your PvP Timer off!");
            return;
        }

        boolean charge = team != LandBoard.getInstance().getTeam(sender.getLocation()) && !HCF.getInstance().getConfig().getBoolean("legions");

        if (charge && team.getBalance() < (HCF.getInstance().getServerHandler().isHardcore() ? 20 : 50)) {
            sender.sendMessage(ChatColor.RED + "Your team needs at least $" + (HCF.getInstance().getServerHandler().isHardcore() ? 20 : 50) + " to teleport to your team headquarters.");
            return;
        }

        HCF.getInstance().getServerHandler().beginHQWarp(sender, team, 10, charge);
    }
}

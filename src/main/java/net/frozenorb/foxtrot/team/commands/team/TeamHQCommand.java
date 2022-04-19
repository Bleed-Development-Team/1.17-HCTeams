package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamHQCommand {

    @Command(value={ "team hq", "t hq", "f hq", "faction hq", "fac hq", "team home", "t home", "f home", "faction home", "fac home", "home", "hq" })
    public static void teamHQ(@Sender Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.DARK_AQUA + "You are not on a team!");
            return;
        }

        if (team.getHQ() == null) {
            sender.sendMessage(ChatColor.RED + "HQ not set.");
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters during the End of the World!");
            return;
        }

        if (sender.hasMetadata("frozen")) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters while you're frozen!");
            return;
        }

        if (Foxtrot.getInstance().getInDuelPredicate().test(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a duel!");
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Use /pvp enable to toggle your PvP Timer off!");
            return;
        }

        boolean charge = team != LandBoard.getInstance().getTeam(sender.getLocation()) && !Foxtrot.getInstance().getConfig().getBoolean("legions");
        
        if (charge && team.getBalance() < (Foxtrot.getInstance().getServerHandler().isHardcore() ? 20 : 50)) {
            sender.sendMessage(ChatColor.RED + "Your team needs at least $" + (Foxtrot.getInstance().getServerHandler().isHardcore() ? 20 : 50) + " to teleport to your team headquarters.");
            return;
        }

        Foxtrot.getInstance().getServerHandler().beginHQWarp(sender, team, 10, charge);
    }
}
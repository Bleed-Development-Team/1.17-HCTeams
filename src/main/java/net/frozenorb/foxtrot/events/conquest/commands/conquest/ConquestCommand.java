package net.frozenorb.foxtrot.events.conquest.commands.conquest;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.events.conquest.game.ConquestGame;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class ConquestCommand {

    @Command(value={ "conquest" })
    public static void conquest(@Sender Player sender) {
        ConquestGame game = Foxtrot.getInstance().getConquestHandler().getGame();

        if (game == null) {
            sender.sendMessage(ChatColor.RED + "Conquest is not active.");
            return;
        }

        Map<ObjectId, Integer> caps = game.getTeamPoints();

        sender.sendMessage(ChatColor.YELLOW + "Conquest Scores:");
        boolean sent = false;

        for (Map.Entry<ObjectId, Integer> capEntry : caps.entrySet()) {
            Team resolved = Foxtrot.getInstance().getTeamHandler().getTeam(capEntry.getKey());

            if (resolved != null) {
                sender.sendMessage(resolved.getName(sender) + ": " + ChatColor.WHITE + capEntry.getValue() + " point" + (capEntry.getValue() == 1 ? "" : "s"));
                sent = true;
            }
        }

        if (!sent) {
            sender.sendMessage(ChatColor.GRAY + "No points have been scored!");
        }

        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW.toString() + ConquestHandler.getPointsToWin() + " points are required to win.");
    }

}
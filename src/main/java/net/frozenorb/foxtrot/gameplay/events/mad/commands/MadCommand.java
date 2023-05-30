package net.frozenorb.foxtrot.gameplay.events.mad.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.mad.MadHandler;
import net.frozenorb.foxtrot.gameplay.events.mad.game.MadGame;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;


@CommandAlias("mad")
public class MadCommand extends BaseCommand {

    @Default
    public static void conquest(Player sender) {
        MadGame game = HCF.getInstance().getMadHandler().getGame();

        if (game == null) {
            sender.sendMessage(ChatColor.RED + "Mad is not active.");
            return;
        }

        Map<ObjectId, Integer> caps = game.getTeamPoints();

        sender.sendMessage(ChatColor.YELLOW + "Mad Scores:");
        boolean sent = false;

        for (Map.Entry<ObjectId, Integer> capEntry : caps.entrySet()) {
            Team resolved = HCF.getInstance().getTeamHandler().getTeam(capEntry.getKey());

            if (resolved != null) {
                sender.sendMessage(resolved.getName(sender) + ": " + ChatColor.WHITE + capEntry.getValue() + " point" + (capEntry.getValue() == 1 ? "" : "s"));
                sent = true;
            }
        }

        if (!sent) {
            sender.sendMessage(ChatColor.GRAY + "No points have been scored!");
        }

        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW.toString() + MadHandler.getCapsToWin() + " points are required to win.");
    }

    @Subcommand("start")
    @CommandPermission("mad.start")
    public static void start(Player player){
        Event event = HCF.getInstance().getEventHandler().getEvent("Mad");

        if (event == null) {
            player.sendMessage(CC.translate("&cThe mad event isn't setup."));
            return;
        } else if (event.isActive()) {
            player.sendMessage(CC.translate("&cAlready active."));
            return;
        }

        new MadGame();
    }

    @Subcommand("stop")
    @CommandPermission("mad.start")
    public static void stop(Player player){
        Event event = HCF.getInstance().getEventHandler().getEvent("Mad");

        if (event == null) {
            player.sendMessage(CC.translate("&cThe mad event isn't setup."));
            return;
        } else if (!event.isActive()) {
            player.sendMessage(CC.translate("&cNot active."));
            return;
        }

        HCF.getInstance().getMadHandler().getGame().endGame(null);
    }

}
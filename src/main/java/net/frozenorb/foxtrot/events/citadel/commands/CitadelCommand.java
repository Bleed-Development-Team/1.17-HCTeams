package net.frozenorb.foxtrot.events.citadel.commands;

import com.google.common.base.Joiner;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

public class CitadelCommand {

    // Make this pretty.
    @Command(value={ "citadel" })
    public static void citadel(@Sender Player sender) {
        Set<ObjectId> cappers = Foxtrot.getInstance().getCitadelHandler().getCappers();
        Set<String> capperNames = new HashSet<>();

        for (ObjectId capper : cappers) {
            Team capperTeam = Foxtrot.getInstance().getTeamHandler().getTeam(capper);

            if (capperTeam != null) {
                capperNames.add(capperTeam.getName());
            }
        }

        if (!capperNames.isEmpty()) {
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was captured by " + ChatColor.GREEN + Joiner.on(", ").join(capperNames) + ChatColor.YELLOW + ".");
        } else {
            Event citadel = Foxtrot.getInstance().getEventHandler().getEvent("Citadel");

            if (citadel != null && citadel.isActive()) {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel can be captured now.");
            } else {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was not captured last week.");
            }
        }

        Date lootable = Foxtrot.getInstance().getCitadelHandler().getLootable();
        sender.sendMessage(ChatColor.GOLD + "Citadel: " + ChatColor.WHITE + "Lootable " + (lootable.before(new Date()) ? "now" : "at " + (new SimpleDateFormat()).format(lootable) + (capperNames.isEmpty() ? "." : ", and lootable now by " + Joiner.on(", ").join(capperNames) + ".")));
    }

    @Command(value = "citadel help")
    @Permission(value = "op")
    public static void citadelHelp(@Sender Player player){
        List<String> lines = new ArrayList<>();

        lines.add("&c/citadel loadloottable - Loads the citadel loot in your inventory.");
        lines.add("&c/citadel rescanchests - [Developer use only]");
        lines.add("&c/citadel respawnchests - Respawn all chests inside of citadel");
        lines.add("&c/citadel save - Saves all information about Citadel in a config file.");
        lines.add("&c/citadel saveloottable - Saves the loot.");
        lines.add("&c/citadel setcapper - Sets the citadel cappers.");

        CC.send(lines, player);
    }

}
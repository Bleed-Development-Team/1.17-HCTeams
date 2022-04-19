package net.frozenorb.foxtrot.events.citadel.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.citadel.CitadelHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelRespawnChestsCommand {

    @Command(value ={"citadel respawnchests"})
    @Permission(value = "op")
    public static void citadelRespawnChests(@Sender Player sender) {
        Foxtrot.getInstance().getCitadelHandler().respawnCitadelChests();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Respawned all Citadel chests.");
    }

}
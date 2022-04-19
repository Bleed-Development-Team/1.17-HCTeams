package net.frozenorb.foxtrot.events.citadel.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.citadel.CitadelHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelRescanChestsCommand {

    @Command(value ={"citadel rescanchests"})
    @Permission(value = "op")
    public static void citadelRescanChests(@Sender Player sender) {
        Foxtrot.getInstance().getCitadelHandler().scanLoot();
        Foxtrot.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Rescanned all Citadel chests.");
    }

}
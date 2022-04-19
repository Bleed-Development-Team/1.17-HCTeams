package net.frozenorb.foxtrot.events.citadel.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.citadel.CitadelHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelSaveCommand {

    @Command(value ={"citadel save"})
    @Permission(value = "op")
    public static void citadelSave(@Sender Player sender) {
        Foxtrot.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Saved Citadel info to file.");
    }

}
package net.frozenorb.foxtrot.events.dtc.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import net.frozenorb.foxtrot.events.dtc.DTC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DTCCreateCommand {

    @Command(value ={ "DTC Create" })
    @Permission(value = "foxtrot.dtc.admin")
    public static void kothCreate(Player sender, @Name("dtc") String koth) {
        new DTC(koth, sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created a DTC named " + koth + ".");
    }

}

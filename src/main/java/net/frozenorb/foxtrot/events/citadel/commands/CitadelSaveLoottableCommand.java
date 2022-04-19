package net.frozenorb.foxtrot.events.citadel.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.citadel.CitadelHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CitadelSaveLoottableCommand {

    @Command(value ={"citadel saveloottable"})
    @Permission(value = "op")
    public static void citadelSaveLoottable(@Sender Player sender) {
        Foxtrot.getInstance().getCitadelHandler().getCitadelLoot().clear();

        for (ItemStack itemStack : sender.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                Foxtrot.getInstance().getCitadelHandler().getCitadelLoot().add(itemStack);
            }
        }

        Foxtrot.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Saved Citadel loot from your inventory.");
    }

}
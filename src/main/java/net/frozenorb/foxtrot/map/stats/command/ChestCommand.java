package net.frozenorb.foxtrot.map.stats.command;

import lombok.Getter;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChestCommand {

    @Getter private static final Set<UUID> BYPASS = new HashSet<>();

    @Command(value = {"chest"})
    public static void chest(@Sender Player sender) {
        if (!Foxtrot.getInstance().getServerHandler().isVeltKitMap() && !Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }
        
        if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
            sender.sendMessage(ChatColor.RED + "You can only do this at spawn.");
            return;
        }

        BYPASS.add(sender.getUniqueId());
        sender.openInventory(sender.getEnderChest());
        BYPASS.remove(sender.getUniqueId());
    }

}

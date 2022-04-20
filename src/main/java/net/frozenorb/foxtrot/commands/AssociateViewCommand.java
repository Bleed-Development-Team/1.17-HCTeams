package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CommandAlias("assview|associateview")
@CommandPermission("op")
public class AssociateViewCommand extends BaseCommand {


    @Default
    public void associate(Player sender, @Flags("other") Player target) {
        UUID player2 = null;
        if( Foxtrot.getInstance().getWhitelistedIPMap().contains(target.getUniqueId())) {
            player2 = Foxtrot.getInstance().getWhitelistedIPMap().get(target.getUniqueId());
        }

        Map<UUID, UUID> map = Foxtrot.getInstance().getWhitelistedIPMap().getMap();
        List<String> list = new ArrayList<String>();
        for( UUID id : map.keySet() ) {
            UUID found = map.get(id);
            if( found.equals(player2) ) {
                sender.sendMessage( ChatColor.RED + Bukkit.getOfflinePlayer(id).getName() + ChatColor.YELLOW + " is associated!");
            }
        }
    }
}

package net.frozenorb.foxtrot.commands.gameplay;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.HCF;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("cobble|cobblestone")
public class CobbleCommand extends BaseCommand {

    @Default
    public static void cobble(Player sender) {
        boolean val = !HCF.getInstance().getCobblePickupMap().isCobblePickup(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to pick up cobblestone while in Miner class!");
        HCF.getInstance().getCobblePickupMap().setCobblePickup(sender.getUniqueId(), val);
    }

}

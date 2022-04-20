package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@CommandAlias("kothrewardkey")
@CommandPermission("op")
public class KOTHRewardKeyCommand extends BaseCommand {


    @Default
    public static void kothRewardKey(Player sender, @Flags("other") Player player, String koth, int amount , int tier) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Unable to locate player.");
            return;
        }

        if (amount == 0 || 32 < amount) {
            sender.sendMessage(ChatColor.RED + "Illegal amount! Must be between 1 and 32.");
            return;
        }

        if (tier == 0 || tier > 3) {
            sender.sendMessage(ChatColor.RED + "Illegal tier! Must be between 1 and 3.");
            return;
        }

        ItemStack stack = InventoryUtils.generateKOTHRewardKey(koth, tier);
        stack.setAmount(amount);
        Map<Integer, ItemStack> failed = player.getInventory().addItem(stack);

        if (amount == 1) {
            String msg = ChatColor.YELLOW + "Gave " + player.getName() + " a KOTH reward key." + failed == null || failed.isEmpty() ? "" : " " + failed.size() + " didn't fit.";
            org.bukkit.command.Command.broadcastCommandMessage(sender, msg);
            sender.sendMessage(msg);
        } else {
            String msg = ChatColor.YELLOW + "Gave " + player.getName() + " " + amount + " KOTH reward keys." + failed == null || failed.isEmpty() ? "" : " " + failed.size() + " didn't fit.";
            org.bukkit.command.Command.broadcastCommandMessage(sender, msg);
            sender.sendMessage(msg);
        }
    }

}
package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandAlias("rename")
@CommandPermission("op")
public class RenameCommand extends BaseCommand {
    @Default
    public void onRename(Player sender, @Name("rename") String name) {
        ItemStack baseStack = sender.getInventory().getItemInMainHand();

        boolean canColor = sender.hasPermission("core.rename.colored");

        if (baseStack.getItemMeta() == null) {
            sender.sendMessage(CC.RED + "Must be holding an item with a valid ItemMeta");
            return;
        }


        ItemMeta finalmeta = baseStack.getItemMeta();

        finalmeta.setDisplayName(canColor ? CC.translateHex(name) : name);
        baseStack.setItemMeta(finalmeta);
        sender.updateInventory();

        sender.sendMessage(CC.GREEN + "Item renamed to " + (canColor ? CC.translateHex(name) : name));
    }
}

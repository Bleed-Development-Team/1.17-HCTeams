package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("rename")
@CommandPermission("op")
public class RenameCommand extends BaseCommand {

    @Default
    public void onDefault(Player sender, String name) {
        sender.getInventory().getItemInMainHand().getItemMeta().setDisplayName(CC.translateHex(name));
        sender.sendMessage(CC.GREEN + "Item renamed to " + CC.translateHex(name));
    }
}

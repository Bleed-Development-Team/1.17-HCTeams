package net.frozenorb.foxtrot.extras.blockshop.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.extras.blockshop.BlockShop;
import org.bukkit.entity.Player;

@CommandAlias("blockshop|shop")
public class BlockShopCommand extends BaseCommand {

    @Default
    public static void blockShop(Player player){
        new BlockShop(player).updateMenu();
    }
}

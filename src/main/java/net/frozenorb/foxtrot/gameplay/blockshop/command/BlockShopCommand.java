package net.frozenorb.foxtrot.gameplay.blockshop.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.gameplay.blockshop.BlockShop;
import org.bukkit.entity.Player;

@CommandAlias("blockshop|shop")
public class BlockShopCommand extends BaseCommand {

    @Default
    public static void blockShop(Player player){
        new BlockShop(player).updateMenu();
    }
}

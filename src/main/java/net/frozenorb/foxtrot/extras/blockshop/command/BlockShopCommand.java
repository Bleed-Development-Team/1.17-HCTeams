package net.frozenorb.foxtrot.extras.blockshop.command;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.extras.blockshop.BlockShop;
import org.bukkit.entity.Player;

public class BlockShopCommand {

    @Command(value = {"blockshop", "shop"})
    public static void blockShop(@Sender Player player){
        new BlockShop(player).updateMenu();
    }
}

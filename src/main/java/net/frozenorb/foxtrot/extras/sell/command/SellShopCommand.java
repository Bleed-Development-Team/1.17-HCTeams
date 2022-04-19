package net.frozenorb.foxtrot.extras.sell.command;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.extras.sell.SellMenu;
import org.bukkit.entity.Player;

public class SellShopCommand {

    @Command(value = {"sell", "sellshop"})
    public static void sell(@Sender Player player){
        new SellMenu(player).updateMenu();
    }
}

package net.frozenorb.foxtrot.extras.sell.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.extras.sell.SellMenu;
import org.bukkit.entity.Player;

@CommandAlias("sell|sellshop")
public class SellShopCommand extends BaseCommand {

    @Default
    public static void sell(Player player){
        new SellMenu(player).updateMenu();
    }
}

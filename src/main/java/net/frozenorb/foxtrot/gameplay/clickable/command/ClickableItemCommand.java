package net.frozenorb.foxtrot.gameplay.clickable.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.clickable.ClickableItem;
import net.frozenorb.foxtrot.gameplay.clickable.ClickableItemHandler;
import net.frozenorb.foxtrot.gameplay.clickable.type.GemsPack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("clickableitem")
@CommandPermission("op")
public class ClickableItemCommand extends BaseCommand {

    @Default
    public void tes(Player player){
        player.getInventory().addItem(HCF.getInstance().getClickableItemHandler().clickableItems.get(0).getItemStack());
    }

    @Subcommand("give")
    public void give(Player player, @Name("item") String name){
        ClickableItem item = HCF.getInstance().getClickableItemHandler().getClickableItem(name);

        if (item == null) return;

        player.getInventory().addItem(item.getItemStack());
    }
}

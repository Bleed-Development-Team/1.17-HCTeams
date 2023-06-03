package net.frozenorb.foxtrot.gameplay.clickable.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.clickable.ClickableItem;
import net.frozenorb.foxtrot.gameplay.clickable.ClickableItemHandler;
import net.frozenorb.foxtrot.gameplay.clickable.type.GemsPack;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.command.CommandSender;
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
    public void give(CommandSender player, @Flags("other") @Name("player") Player target, @Flags("other") @Name("item") String name, @Optional Integer amount1){
        int amount = (amount1 == null ? 1 : amount1);

        ClickableItem item = HCF.getInstance().getClickableItemHandler().getClickableItem(name);

        if (item == null) return;

        target.getInventory().addItem(ItemBuilder.copyOf(item.getItemStack().clone()).amount(amount).build());
    }
}

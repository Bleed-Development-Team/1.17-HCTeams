package net.frozenorb.foxtrot.gameplay.blockshop.impl;

import io.github.nosequel.menu.Menu;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import net.frozenorb.foxtrot.gameplay.blockshop.button.BlockShopButton;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChristmasBlocksMenu extends Menu {

    public ChristmasBlocksMenu(Player player){
        super(player, "Christmas Blocks", 9 * 3);
    }

    @Override
    public void tick() {
        Player player = getPlayer();
        this.buttons[12] = new BlockShopButton(player, Material.SNOW_BLOCK, "&f&lSnow Blocks", 32, 50).setClickAction(event -> purchaseItem(Material.SNOW_BLOCK, event));
        this.buttons[13] = new BlockShopButton(player, Material.PUMPKIN, "&6&lPumpkins", 32, 50).setClickAction(event -> purchaseItem(Material.SNOW_BLOCK, event));
        this.buttons[14] = new BlockShopButton(player, Material.SNOW, "&f&Snow", 32, 50).setClickAction(event -> purchaseItem(Material.SNOW_BLOCK, event));
    }


    public void purchaseItem(Material material, InventoryClickEvent event){
        if (material == null) return;
        event.setCancelled(true);
        Player player = getPlayer();

        if (EconomyHandler.getBalance(getPlayer().getUniqueId()) >= 50){
            ItemStack itemStack = new ItemStack(material);
            itemStack.setType(material);
            itemStack.setAmount(32);

            player.getInventory().addItem(itemStack);

            player.sendMessage(CC.translate("&aYou have successfully purchased x" + 32 + " of " + ItemUtils.getName(itemStack)));
            EconomyHandler.setBalance(player.getUniqueId(), EconomyHandler.getBalance(player.getUniqueId()) - 50);

        } else {
            player.sendMessage(CC.translate("&cYou don't have enough money to purchase this item."));
        }
    }

}

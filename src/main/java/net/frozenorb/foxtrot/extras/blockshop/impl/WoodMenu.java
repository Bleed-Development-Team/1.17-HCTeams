package net.frozenorb.foxtrot.extras.blockshop.impl;

import io.github.nosequel.menu.Menu;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.extras.blockshop.button.BlockShopButton;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class WoodMenu extends Menu {

    public WoodMenu(Player player) {
        super(player, "Wood Shop", 9 * 3);

    }

    @Override
    public void tick() {
        this.buttons[11] = new BlockShopButton(getPlayer(), Material.OAK_LOG, "&6Oak Wood", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[12] = new BlockShopButton(getPlayer(), Material.SPRUCE_LOG, "&6Spruce Wood", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[13] = new BlockShopButton(getPlayer(), Material.JUNGLE_LOG, "&6Jungle Wood", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[14] = new BlockShopButton(getPlayer(), Material.ACACIA_LOG, "&6Acacia Blue Wood", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[15] = new BlockShopButton(getPlayer(), Material.DARK_OAK_LOG, "&6Dark Oak Wood", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
    }

    public void purchaseItem(Material material, InventoryClickEvent event){
        if (material == null) return;
        event.setCancelled(true);
        Player player = getPlayer();
        if (FrozenEconomyHandler.getBalance(getPlayer().getUniqueId()) >= 50){
            ItemStack itemStack = new ItemStack(material);
            itemStack.setType(material);
            itemStack.setAmount(32);

            player.getInventory().addItem(itemStack);

            player.sendMessage(CC.translate("&aYou have successfully purchased x" + 32 + " of " + ItemUtils.getName(itemStack)));
            FrozenEconomyHandler.setBalance(player.getUniqueId(), FrozenEconomyHandler.getBalance(player.getUniqueId()) - 50);

        } else {
            player.sendMessage(CC.translate("&cYou don't have enough money to purchase this item."));
        }
    }

}

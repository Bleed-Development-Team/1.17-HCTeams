package net.frozenorb.foxtrot.extras.blockshop.impl;

import io.github.nosequel.menu.Menu;
import net.frozenorb.foxtrot.extras.blockshop.button.BlockShopButton;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class WoolMenu extends Menu {

    public WoolMenu(Player player) {
        super(player, "Wool Shop", 9 * 5);

    }

    @Override
    public void tick() {
        this.buttons[10] = new BlockShopButton(getPlayer(), Material.WHITE_WOOL, "White Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[11] = new BlockShopButton(getPlayer(), Material.ORANGE_WOOL, "&6Orange Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[12] = new BlockShopButton(getPlayer(), Material.MAGENTA_WOOL, "&5Magenta Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[13] = new BlockShopButton(getPlayer(), Material.LIGHT_BLUE_WOOL, "&bLight Blue Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[14] = new BlockShopButton(getPlayer(), Material.YELLOW_WOOL, "&eYellow Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[15] = new BlockShopButton(getPlayer(), Material.LIME_WOOL, "&aGreen Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[16] = new BlockShopButton(getPlayer(), Material.GRAY_WOOL, "&8Gray Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));

        this.buttons[20] = new BlockShopButton(getPlayer(), Material.BROWN_WOOL, "&eYellow Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[21] = new BlockShopButton(getPlayer(), Material.GREEN_WOOL, "&2Dark Green Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[22] = new BlockShopButton(getPlayer(), Material.RED_WOOL, "&cRed Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[23] = new BlockShopButton(getPlayer(), Material.PINK_WOOL, "&dPink Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[24] = new BlockShopButton(getPlayer(), Material.LIGHT_GRAY_WOOL, "&7Light Gray Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[31] = new BlockShopButton(getPlayer(), Material.PURPLE_WOOL, "&5Purple Wool", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
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

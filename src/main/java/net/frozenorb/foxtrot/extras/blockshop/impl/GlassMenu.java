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

public class GlassMenu extends Menu {

    public GlassMenu(Player player) {
        super(player, "Glass Shop", 9 * 5);

    }

    @Override
    public void tick() {
        this.buttons[10] = new BlockShopButton(getPlayer(), Material.GLASS, "Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[11] = new BlockShopButton(getPlayer(), Material.WHITE_STAINED_GLASS, "&f&lWhite Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[12] = new BlockShopButton(getPlayer(), Material.ORANGE_STAINED_GLASS, "&6Orange Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[13] = new BlockShopButton(getPlayer(), Material.MAGENTA_STAINED_GLASS, "&5Magenta Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[14] = new BlockShopButton(getPlayer(), Material.LIGHT_BLUE_STAINED_GLASS, "&bLight Blue Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[15] = new BlockShopButton(getPlayer(), Material.YELLOW_STAINED_GLASS, "&eYellow Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[16] = new BlockShopButton(getPlayer(), Material.RED_STAINED_GLASS, "&cRed Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));

        this.buttons[20] = new BlockShopButton(getPlayer(), Material.BROWN_STAINED_GLASS, "&6Brown Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[21] = new BlockShopButton(getPlayer(), Material.BLUE_STAINED_GLASS, "&9Blue Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[22] = new BlockShopButton(getPlayer(), Material.GRAY_STAINED_GLASS, "&7Gray Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[23] = new BlockShopButton(getPlayer(), Material.TINTED_GLASS, "&8Dark Gray Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[24] = new BlockShopButton(getPlayer(), Material.PINK_STAINED_GLASS, "&dPink Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[31] = new BlockShopButton(getPlayer(), Material.LIME_STAINED_GLASS, "&aGreen Glass", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
    }


    public void purchaseItem(Material material, InventoryClickEvent event) {
        if (material == null) return;
        Player player = getPlayer();
        event.setCancelled(true);
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

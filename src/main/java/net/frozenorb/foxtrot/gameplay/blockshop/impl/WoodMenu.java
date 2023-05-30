package net.frozenorb.foxtrot.gameplay.blockshop.impl;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import net.frozenorb.foxtrot.gameplay.blockshop.BlockShop;
import net.frozenorb.foxtrot.gameplay.blockshop.button.BackButton;
import net.frozenorb.foxtrot.gameplay.blockshop.button.BlockShopButton;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class WoodMenu extends Menu {

    public WoodMenu(Player player) {
        super(player, CC.translate("&6Wood Shop"), 9 * 5);

    }

    @Override
    public void tick() {

        this.buttons[0] = new Button(glass());
        this.buttons[1] = new Button(glass());
        this.buttons[9] = new Button(glass());

        this.buttons[7] = new Button(glass());
        this.buttons[8] = new Button(glass());
        this.buttons[17] = new Button(glass());


        this.buttons[27] = new Button(glass());
        this.buttons[35] = new Button(glass());
        this.buttons[36] = new Button(glass());

        this.buttons[37] = new Button(glass());
        this.buttons[43] = new Button(glass());
        this.buttons[44] = new Button(glass());

        this.buttons[40] = new BackButton().setClickAction(event -> new BlockShop(getPlayer()).updateMenu());

        this.buttons[11] = new BlockShopButton(getPlayer(), Material.OAK_LOG, "&6Oak Wood", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[12] = new BlockShopButton(getPlayer(), Material.SPRUCE_LOG, "&6Spruce Wood", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[13] = new BlockShopButton(getPlayer(), Material.JUNGLE_LOG, "&6Jungle Wood", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[14] = new BlockShopButton(getPlayer(), Material.ACACIA_LOG, "&6Acacia Blue Wood", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
        this.buttons[15] = new BlockShopButton(getPlayer(), Material.DARK_OAK_LOG, "&6Dark Oak Wood", 32, 50).setClickAction(event -> purchaseItem(event.getCurrentItem().getType(), event));
    }

    private ItemStack glass(){
        return ItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
                .name(" ")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
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

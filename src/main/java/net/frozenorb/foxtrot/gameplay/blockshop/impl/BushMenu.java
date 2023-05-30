package net.frozenorb.foxtrot.gameplay.blockshop.impl;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import net.frozenorb.foxtrot.gameplay.blockshop.BlockShop;
import net.frozenorb.foxtrot.gameplay.blockshop.button.BackButton;
import net.frozenorb.foxtrot.gameplay.blockshop.button.BuyButton;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class BushMenu extends Menu {

    public BushMenu(Player player){
        super(player, CC.translate("&6Bush Blocks"), 9 * 5);

        player.updateInventory();
    }

    private ItemStack glass(){
        return ItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
                .name(" ")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public void tick() {
        this.buttons[0] = new Button(glass());
        this.buttons[1] = new Button(glass());
        this.buttons[9] = new Button(glass());

        this.buttons[7] = new Button(glass());
        this.buttons[8] = new Button(glass());
        this.buttons[17] = new Button(glass());

        this.buttons[12] = new BuyButton("Lilac", 150, 16, Material.LILAC).setClickAction(event -> buy(event, Material.LILAC, 150));
        this.buttons[13] = new BuyButton("Double Tallgrass", 100, 16, Material.TALL_GRASS).setClickAction(event -> buy(event, Material.TALL_GRASS, 100));
        this.buttons[14] = new BuyButton("Large Fern", 125, 16, Material.LARGE_FERN).setClickAction(event -> buy(event, Material.LARGE_FERN, 125));

        this.buttons[21] = new BuyButton("Peony", 150, 16, Material.PEONY).setClickAction(event -> buy(event, Material.PEONY, 150));
        this.buttons[22] = new BuyButton("Dead Bush", 200, 16, Material.TALL_GRASS).setClickAction(event -> buy(event, Material.DEAD_BUSH, 200));
        this.buttons[23] = new BuyButton("Fern", 125, 16, Material.LARGE_FERN).setClickAction(event -> buy(event, Material.FERN, 125));

        this.buttons[30] = new BuyButton("Grass", 125, 16, Material.GRASS).setClickAction(event -> buy(event, Material.GRASS, 125));
        this.buttons[32] = new BuyButton("Rose Bush", 200, 16, Material.ROSE_BUSH).setClickAction(event -> buy(event, Material.ROSE_BUSH, 200));

        this.buttons[27] = new Button(glass());
        this.buttons[35] = new Button(glass());
        this.buttons[36] = new Button(glass());

        this.buttons[37] = new Button(glass());
        this.buttons[43] = new Button(glass());
        this.buttons[44] = new Button(glass());

        this.buttons[40] = new BackButton().setClickAction(event -> new BlockShop(getPlayer()).updateMenu());
    }


    private void buy(InventoryClickEvent event, Material material, int price) {
        Player player = getPlayer();

        event.setCancelled(true);

        if (price <= EconomyHandler.getBalance(player.getUniqueId())) {
            EconomyHandler.withdraw(player.getUniqueId(), price);

            player.getInventory().addItem(new ItemStack(material, 16));
            player.sendMessage(CC.translate("&aPurchase complete! Your new balance is: $" + EconomyHandler.getBalance(player.getUniqueId())));
        } else {
            player.sendMessage(CC.translate("&cYou don't have enough money to purchase this item!"));
        }
    }

}

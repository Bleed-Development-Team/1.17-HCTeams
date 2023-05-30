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

public class StoneMenu extends Menu {

    public StoneMenu(Player player){
        super(player, CC.translate("&6Stone Blocks"), 9 * 5);

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

        this.buttons[13] = new BuyButton("Stone", 300, 16, Material.STONE).setClickAction(event -> buy(event, Material.STONE, 300));
        this.buttons[20] = new BuyButton("Cobblestone Wall", 300, 16, Material.COBBLESTONE_WALL).setClickAction(event -> buy(event, Material.COBBLESTONE_WALL, 300));
        this.buttons[21] = new BuyButton("Cracked Stone Bricks", 700, 16, Material.CRACKED_STONE_BRICKS).setClickAction(event -> buy(event, Material.CRACKED_STONE_BRICKS, 700));
        this.buttons[22] = new BuyButton("Mossy Stone Bricks", 900, 16, Material.MOSSY_STONE_BRICKS).setClickAction(event -> buy(event, Material.MOSSY_STONE_BRICKS, 900));
        this.buttons[23] = new BuyButton("Chiseled Stone Bricks", 350, 16, Material.CHISELED_STONE_BRICKS).setClickAction(event -> buy(event, Material.CHISELED_STONE_BRICKS, 350));
        this.buttons[24] = new BuyButton("Mossy Cobblestone Wall", 750, 16, Material.MOSSY_COBBLESTONE_WALL).setClickAction(event -> buy(event, Material.MOSSY_COBBLESTONE_WALL, 750));

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

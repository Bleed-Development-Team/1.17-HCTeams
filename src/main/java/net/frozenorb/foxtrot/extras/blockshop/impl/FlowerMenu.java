package net.frozenorb.foxtrot.extras.blockshop.impl;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.extras.blockshop.BlockShop;
import net.frozenorb.foxtrot.extras.blockshop.button.BackButton;
import net.frozenorb.foxtrot.extras.blockshop.button.BuyButton;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class FlowerMenu extends Menu {

    public FlowerMenu(Player player){
        super(player, CC.translate("&6Flowers"), 9 * 5);

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

        this.buttons[12] = new BuyButton("Poppy", 50, 16, Material.POPPY).setClickAction(event -> buy(event, Material.POPPY, 50));
        this.buttons[13] = new BuyButton("Blue Orchid", 90, 16, Material.BLUE_ORCHID).setClickAction(event -> buy(event, Material.BLUE_ORCHID, 90));
        this.buttons[14] = new BuyButton("Sunflower", 25, 16, Material.SUNFLOWER).setClickAction(event -> buy(event, Material.SUNFLOWER, 25));

        this.buttons[20] = new BuyButton("Orange Tulip", 150, 16, Material.ORANGE_TULIP).setClickAction(event -> buy(event, Material.ORANGE_TULIP, 150));
        this.buttons[21] = new BuyButton("Azure Bluet", 30, 16, Material.AZURE_BLUET).setClickAction(event -> buy(event, Material.AZURE_BLUET , 30));
        this.buttons[22] = new BuyButton("White Tulip", 75, 16, Material.WHITE_TULIP).setClickAction(event -> buy(event, Material.WHITE_TULIP , 75));
        this.buttons[23] = new BuyButton("Pink Tulip", 70, 16, Material.PINK_TULIP).setClickAction(event -> buy(event, Material.PINK_TULIP , 70));
        this.buttons[24] = new BuyButton("Oxeye Daisy", 40, 16, Material.OXEYE_DAISY).setClickAction(event -> buy(event, Material.OXEYE_DAISY , 40));

        this.buttons[30] = new BuyButton("Dandelion", 30, 16, Material.DANDELION).setClickAction(event -> buy(event, Material.DANDELION , 30));
        this.buttons[31] = new BuyButton("Red Tulip", 90, 16, Material.RED_TULIP).setClickAction(event -> buy(event, Material.RED_TULIP , 90));
        this.buttons[32] = new BuyButton("Allium", 90, 16, Material.ALLIUM).setClickAction(event -> buy(event, Material.ALLIUM , 90));

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

        if (price <= FrozenEconomyHandler.getBalance(player.getUniqueId())) {
            FrozenEconomyHandler.withdraw(player.getUniqueId(), price);

            player.getInventory().addItem(new ItemStack(material, 16));
            player.sendMessage(CC.translate("&aPurchase complete! Your new balance is: $" + FrozenEconomyHandler.getBalance(player.getUniqueId())));
        } else {
            player.sendMessage(CC.translate("&cYou don't have enough money to purchase this item!"));
        }
    }

}

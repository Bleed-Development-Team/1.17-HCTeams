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

public class GlassMenu extends Menu {

    public GlassMenu(Player player){
        super(player, CC.translate("&6Glass Blocks"), 9 * 6);

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

        this.buttons[12] = new BuyButton("Orange Stained Glass", 350, 16, Material.ORANGE_STAINED_GLASS).setClickAction(event -> buy(event, Material.ORANGE_STAINED_GLASS, 350));
        this.buttons[13] = new BuyButton("Magenta Stained Glass", 350, 16, Material.MAGENTA_STAINED_GLASS).setClickAction(event -> buy(event, Material.MAGENTA_STAINED_GLASS, 350));
        this.buttons[14] = new BuyButton("Light Blue Stained Glass", 350, 16, Material.LIGHT_BLUE_STAINED_GLASS).setClickAction(event -> buy(event, Material.LIGHT_BLUE_STAINED_GLASS, 350));

        this.buttons[20] = new BuyButton("Lime Stained Glass", 350, 16, Material.LIME_STAINED_GLASS).setClickAction(event -> buy(event, Material.LIME_STAINED_GLASS, 350));
        this.buttons[21] = new BuyButton("Pink Stained Glass", 350, 16, Material.PINK_STAINED_GLASS).setClickAction(event -> buy(event, Material.PINK_STAINED_GLASS, 350));
        this.buttons[22] = new BuyButton("Gray Stained Glass", 350, 16, Material.GRAY_STAINED_GLASS).setClickAction(event -> buy(event, Material.GRAY_STAINED_GLASS, 350));
        this.buttons[23] = new BuyButton("Light Gray Stained Glass", 350, 16, Material.LIGHT_GRAY_STAINED_GLASS).setClickAction(event -> buy(event, Material.LIGHT_GRAY_STAINED_GLASS, 350));
        this.buttons[24] = new BuyButton("Cyan Stained Glass", 350, 16, Material.CYAN_STAINED_GLASS).setClickAction(event -> buy(event, Material.CYAN_STAINED_GLASS, 350));

        this.buttons[29] = new BuyButton("Purple Stained Glass", 350, 16, Material.PURPLE_STAINED_GLASS).setClickAction(event -> buy(event, Material.PURPLE_STAINED_GLASS, 350));
        this.buttons[30] = new BuyButton("Blue Stained Glass", 350, 16, Material.BLUE_STAINED_GLASS).setClickAction(event -> buy(event, Material.BLUE_STAINED_GLASS, 350));
        this.buttons[31] = new BuyButton("Brown Stained Glass", 350, 16, Material.BROWN_STAINED_GLASS).setClickAction(event -> buy(event, Material.BROWN_STAINED_GLASS, 350));
        this.buttons[32] = new BuyButton("Green Stained Glass", 350, 16, Material.GREEN_STAINED_GLASS).setClickAction(event -> buy(event, Material.GREEN_STAINED_GLASS, 350));
        this.buttons[33] = new BuyButton("Red Stained Glass", 350, 16, Material.RED_STAINED_GLASS).setClickAction(event -> buy(event, Material.RED_STAINED_GLASS, 350));

        this.buttons[39] = new BuyButton("White Stained Glass", 350, 16, Material.WHITE_STAINED_GLASS).setClickAction(event -> buy(event, Material.WHITE_STAINED_GLASS, 350));
        this.buttons[40] = new BuyButton("Yellow Stained Glass", 350, 16, Material.YELLOW_STAINED_GLASS).setClickAction(event -> buy(event, Material.YELLOW_STAINED_GLASS, 350));
        this.buttons[41] = new BuyButton("Black Stained Glass", 350, 16, Material.BLACK_STAINED_GLASS).setClickAction(event -> buy(event, Material.BLACK_STAINED_GLASS, 350));


        this.buttons[44] = new Button(glass());
        this.buttons[36] = new Button(glass());
        this.buttons[46] = new Button(glass());

        this.buttons[49] = new BackButton().setClickAction(event -> new BlockShop(getPlayer()).updateMenu());

        this.buttons[45] = new Button(glass());
        this.buttons[52] = new Button(glass());
        this.buttons[53] = new Button(glass());

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

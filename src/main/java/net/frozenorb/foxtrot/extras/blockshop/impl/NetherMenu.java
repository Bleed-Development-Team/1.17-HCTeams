package net.frozenorb.foxtrot.extras.blockshop.impl;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.extras.blockshop.BlockShop;
import net.frozenorb.foxtrot.extras.blockshop.button.BackButton;
import net.frozenorb.foxtrot.extras.blockshop.button.BuyButton;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class NetherMenu extends Menu {

    public NetherMenu(Player player){
        super(player, CC.translate("&6Nether Blocks"), 9 * 5);

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

        this.buttons[20] = new BuyButton("Nether Brick Slab", 75, 16, Material.NETHER_BRICK_SLAB).setClickAction(event -> buy(event, Material.NETHER_BRICK_SLAB, 250));
        this.buttons[21] = new BuyButton("Nether Brick", 100, 16, Material.NETHER_BRICK).setClickAction(event -> buy(event, Material.NETHER_BRICK, 250));
        this.buttons[22] = new BuyButton("Nether Brick Fence", 80, 16, Material.NETHER_BRICK_FENCE).setClickAction(event -> buy(event, Material.NETHER_BRICK_FENCE, 250));
        this.buttons[23] = new BuyButton("Nether Brick Stairs", 90, 16, Material.NETHER_BRICK_STAIRS).setClickAction(event -> buy(event, Material.NETHER_BRICK_STAIRS , 250));
        this.buttons[24] = new BuyButton("Nether Brick Ingots", 35, 16, Material.NETHER_BRICKS).setClickAction(event -> buy(event, Material.NETHER_BRICKS, 250));

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

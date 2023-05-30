package net.frozenorb.foxtrot.gameplay.blockshop.category;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import net.frozenorb.foxtrot.gameplay.blockshop.BlockShop;
import net.frozenorb.foxtrot.gameplay.blockshop.button.BuyButton;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BuyMenu extends Menu {

    public BuyMenu(Player player) {
        super(player, "Buy Shop", 9 * 5);

        player.updateInventory();
    }

    @Override
    public void tick() {
        this.buttons[4] = new BuyButton("&cCrowbar", 25000, 1, Material.DIAMOND_HOE).setClickAction(event -> buy(event));

        this.buttons[10] = new BuyButton("Melon Seeds", 500, 16, Material.MELON_SEEDS).setClickAction(event -> buy(event, Material.MELON_SEEDS, 500, 16));
        this.buttons[11] = new BuyButton("Carrot", 500, 16, Material.CARROT).setClickAction(event -> buy(event, Material.CARROT, 500, 16));
        this.buttons[12] = new BuyButton("Sugar Cane", 500, 16, Material.SUGAR_CANE).setClickAction(event -> buy(event, Material.SUGAR_CANE, 500, 16));
        this.buttons[13] = new BuyButton("Glistering Melons", 500, 16, Material.GLISTERING_MELON_SLICE).setClickAction(event -> buy(event, Material.GLISTERING_MELON_SLICE, 500, 16));
        this.buttons[14] = new BuyButton("Potato", 500, 16, Material.POTATO).setClickAction(event -> buy(event, Material.POTATO, 500, 16));
        this.buttons[15] = new BuyButton("Blaze Rod", 500, 16, Material.BLAZE_ROD).setClickAction(event -> buy(event, Material.BLAZE_ROD, 500, 16));
        this.buttons[16] = new BuyButton("Nether Wart", 500, 16, Material.NETHER_WART).setClickAction(event -> buy(event, Material.NETHER_WART, 500, 16));

        this.buttons[19] = new BuyButton("Pumpkin Seeds", 500, 16, Material.PUMPKIN_SEEDS).setClickAction(event -> buy(event, Material.PUMPKIN_SEEDS, 500, 16));
        this.buttons[20] = new BuyButton("Slimeball", 500, 16, Material.SLIME_BALL).setClickAction(event -> buy(event, Material.SLIME_BALL, 500, 16));
        this.buttons[21] = new BuyButton("End Portal", 5000, 1, Material.END_PORTAL_FRAME).setClickAction(event -> buy(event, Material.END_PORTAL_FRAME, 5000, 1));
        this.buttons[22] = new BuyButton("Bottle o' Enchanting", 5000, 16, Material.EXPERIENCE_BOTTLE).setClickAction(event -> buy(event, Material.EXPERIENCE_BOTTLE, 5000, 16));
        this.buttons[23] = new BuyButton("Ghast Tear", 1000, 16, Material.GHAST_TEAR).setClickAction(event -> buy(event, Material.GHAST_TEAR, 1000, 16));
        this.buttons[24] = new BuyButton("Cow Egg", 1000, 2, Material.COW_SPAWN_EGG).setClickAction(event -> buy(event, Material.COW_SPAWN_EGG, 1000, 2));
        this.buttons[25] = new BuyButton("Cocoa Beans", 500, 16, Material.COCOA_BEANS).setClickAction(event -> buy(event, Material.COCOA_BEANS, 500, 16));

        this.buttons[29] = new BuyButton("Ink Sac", 1000, 16, Material.INK_SAC).setClickAction(event -> buy(event, Material.INK_SAC, 1000, 16));
        this.buttons[20] = new BuyButton("Fire Charge", 1000, 16, Material.FIRE_CHARGE).setClickAction(event -> buy(event, Material.FIRE_CHARGE, 1000, 16));
        this.buttons[21] = new BuyButton("Beacon", 30000, 1, Material.BEACON).setClickAction(event -> buy(event, Material.BEACON, 30000, 1));
        this.buttons[22] = new BuyButton("Coal", 1000, 16, Material.COAL).setClickAction(event -> buy(event, Material.COAL, 1000, 16));
        this.buttons[23] = new BuyButton("Feather", 500, 16, Material.FEATHER).setClickAction(event -> buy(event, Material.FEATHER, 500, 16));

        int x = 28;

        for (int i = 0; i < 7; i++) {
            this.buttons[x++] = new Button(BlockShop.GLASS);
        }


        this.buttons[39] = new Button(BlockShop.SELL_HEAD).setClickAction(event -> switchCategory("sell", event));
        this.buttons[41] = new Button(BlockShop.BLOCK_SHOP).setClickAction(event -> switchCategory("block", event));
    }

    private void buy(InventoryClickEvent event, Material material, int price, int amount) {
        Player player = getPlayer();

        event.setCancelled(true);

        if (price <= EconomyHandler.getBalance(player.getUniqueId())) {
            EconomyHandler.withdraw(player.getUniqueId(), price);

            player.getInventory().addItem(new ItemStack(material, amount));
            player.sendMessage(CC.translate("&aPurchase complete! Your new balance is: $" + EconomyHandler.getBalance(player.getUniqueId())));
        } else {
            player.sendMessage(CC.translate("&cYou don't have enough money to purchase this item!"));
        }
    }

    private void buy(InventoryClickEvent event) {
        Player player = getPlayer();

        event.setCancelled(true);

        if (25000 <= EconomyHandler.getBalance(player.getUniqueId())) {
            EconomyHandler.withdraw(player.getUniqueId(), 25000);

            player.getInventory().addItem(InventoryUtils.CROWBAR);
            player.sendMessage(CC.translate("&aPurchase complete! Your new balance is: $" + EconomyHandler.getBalance(player.getUniqueId())));
        } else {
            player.sendMessage(CC.translate("&cYou don't have enough money to purchase this item!"));
        }
    }

    private void switchCategory(String category, InventoryClickEvent event){
        Player player = getPlayer();

        event.setCancelled(true);

        switch(category){
            case "block" ->
                    new BlockShop(player).updateMenu();
            case "sell" ->
                    new SellMenu(player).updateMenu();
            case "buy" ->
                    new BuyMenu(player).updateMenu();
        }
    }
}

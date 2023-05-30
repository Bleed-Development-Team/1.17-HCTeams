package net.frozenorb.foxtrot.gameplay.blockshop;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.gameplay.blockshop.button.MainButton;
import net.frozenorb.foxtrot.gameplay.blockshop.category.BuyMenu;
import net.frozenorb.foxtrot.gameplay.blockshop.impl.*;
import net.frozenorb.foxtrot.gameplay.blockshop.category.SellMenu;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BlockShop extends Menu {

    public static ItemStack GLASS = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();


    public static ItemStack BLOCK_SHOP = ItemBuilder.of(Material.PLAYER_HEAD).owner("Super_Sniper", CC.translate("&cBlock Shop"))
            .build();

    public static ItemStack BUY_SHOP = ItemBuilder.of(Material.PLAYER_HEAD).owner("ABigDwarf", CC.translate("&cBuy Shop"))
            .build();

    public static ItemStack SELL_HEAD = ItemBuilder.of(Material.PLAYER_HEAD).owner("TheNewTsar", CC.translate("&cSell Shop"))
            .build();

    public BlockShop(Player player) {
        super(player,"Block Shop", 9 * 5);

        player.updateInventory();

    }

    @Override
    public void tick() {

        this.buttons[3] = new MainButton("&6&lHalloween Blocks", Material.PUMPKIN).setClickAction(event -> new HalloweenMenu(getPlayer()).updateMenu());
        this.buttons[4] = new MainButton("&6&lNether Blocks", Material.NETHER_BRICK_STAIRS).setClickAction(event -> new NetherMenu(getPlayer()).updateMenu());
        this.buttons[5] = new MainButton("&6&lWinter Blocks", Material.ICE).setClickAction(event -> new WinterBlocks(getPlayer()).updateMenu());
        this.buttons[11] = new MainButton("&6&lFlowers", Material.SUNFLOWER).setClickAction(event -> new FlowerMenu(getPlayer()).updateMenu());
        this.buttons[12] = new MainButton("&6&lGlass", Material.WHITE_STAINED_GLASS).setClickAction(event -> new GlassMenu(getPlayer()).updateMenu());
        this.buttons[13] = new MainButton("&6&lQuartz Blocks", Material.QUARTZ_BLOCK).setClickAction(event -> new QuartzMenu(getPlayer()).updateMenu());
        this.buttons[14] = new MainButton("&6&lBush Blocks", Material.DEAD_BUSH).setClickAction(event -> new BushMenu(getPlayer()).updateMenu());
        this.buttons[15] = new MainButton("&6&lWool", Material.WHITE_WOOL).setClickAction(event -> new WoolMenu(getPlayer()).updateMenu());
        this.buttons[21] = new MainButton("&6&lEnd Blocks", Material.END_STONE).setClickAction(event -> new EndMenu(getPlayer()).updateMenu());
        this.buttons[22] = new MainButton("&6&lStone Blocks", Material.STONE_STAIRS).setClickAction(event -> new StoneMenu(getPlayer()).updateMenu());
        this.buttons[22] = new MainButton("&6&lWood", Material.OAK_LOG).setClickAction(event -> new WoodMenu(getPlayer()).updateMenu());

        int x = 28;

        for (int i = 0; i < 7; i++) {
            this.buttons[x++] = new Button(GLASS);
        }

        this.buttons[39] = new Button(SELL_HEAD).setClickAction(event -> switchCategory("sell", event));
        this.buttons[41] = new Button(BUY_SHOP).setClickAction(event -> switchCategory("buy", event));
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

package net.frozenorb.foxtrot.gameplay.blockshop.category;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import net.frozenorb.foxtrot.gameplay.blockshop.BlockShop;
import net.frozenorb.foxtrot.gameplay.blockshop.button.SellButton;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SellMenu extends Menu {

    public SellMenu(Player player) {
        super(player, "Sell Menu", 9 * 4);

        player.updateInventory();
    }

    @Override
    public void tick() {
        this.buttons[3] = new SellButton("Block of Emerald", 2500, 16, Material.EMERALD_BLOCK).setClickAction(event -> sell(Material.EMERALD_BLOCK, event, 2500));
        this.buttons[4] = new SellButton("Block of Diamond", 2250, 16, Material.DIAMOND_BLOCK).setClickAction(event -> sell(Material.DIAMOND_BLOCK, event, 2250));
        this.buttons[5] = new SellButton("Block of Gold", 2000, 16, Material.GOLD_BLOCK).setClickAction(event -> sell(Material.GOLD_BLOCK, event, 2000));

        this.buttons[12] = new SellButton("Block of Lapis", 2000, 16, Material.LAPIS_BLOCK).setClickAction(event -> sell(Material.LAPIS_BLOCK, event, 2000));
        this.buttons[13] = new SellButton("Block of Redstone", 1250, 16, Material.REDSTONE_BLOCK).setClickAction(event -> sell(Material.REDSTONE_BLOCK, event, 1250));
        this.buttons[14] = new SellButton("Block of Iron", 1500, 16, Material.IRON_BLOCK).setClickAction(event -> sell(Material.IRON_BLOCK, event, 1500));

        int x = 19;

        for (int i = 0; i < 7; i++) {
            this.buttons[x++] = new Button(BlockShop.GLASS);
        }

        this.buttons[30] = new Button(BlockShop.BUY_SHOP).setClickAction(event -> switchCategory("buy", event));
        this.buttons[32] = new Button(BlockShop.BLOCK_SHOP).setClickAction(event -> switchCategory("block", event));
    }

    public void sell(Material material, InventoryClickEvent event, int amount) {
        Player player = getPlayer();
        event.setCancelled(true);

        if (player.getInventory().contains(material)){
            List<ItemStack> items = new ArrayList<>();

            for (ItemStack item : player.getInventory()) {
                if (item == null) continue;
                if (item.getType() != material) continue;

                items.add(item);
            }

            ItemStack converted = items.get(0);

            if (converted.getAmount() >= 16){
                converted.setAmount(converted.getAmount() - 16);

                EconomyHandler.deposit(player.getUniqueId(), amount);
                player.sendMessage(CC.translate("&aSell complete! Your new balance is: $" + EconomyHandler.getBalance(player.getUniqueId())));
            } else {
                player.sendMessage(CC.translate("&cYou need at least 16 of this item to sell."));
            }
        } else {
            player.sendMessage(CC.translate("&cYou don't have this item!"));
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

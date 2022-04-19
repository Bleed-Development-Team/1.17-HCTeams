package net.frozenorb.foxtrot.extras.sell;

import io.github.nosequel.menu.Menu;
import net.frozenorb.foxtrot.deathmessage.util.MobUtil;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.extras.sell.button.MainSellButton;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SellMenu extends Menu {

    public SellMenu(Player player) {
        super(player, "Sell Menu", 9);
    }

    @Override
    public void tick() {
        this.buttons[2] = new MainSellButton(Material.DIAMOND_BLOCK, "&a&lDiamond Block").setClickAction(event -> sell(Material.DIAMOND_BLOCK, event, 1000));
        this.buttons[3] = new MainSellButton(Material.EMERALD_BLOCK, "&a&lEmerald Block").setClickAction(event -> sell(Material.EMERALD_BLOCK, event, 1000));
        this.buttons[4] = new MainSellButton(Material.GOLD_BLOCK, "&e&lGold Block").setClickAction(event -> sell(Material.GOLD_BLOCK, event, 1000));
        this.buttons[5] = new MainSellButton(Material.IRON_BLOCK, "&7&lIron Block").setClickAction(event -> sell(Material.IRON_BLOCK, event, 1000));
        this.buttons[6] = new MainSellButton(Material.REDSTONE_BLOCK, "&c&lRedstone Block").setClickAction(event -> sell(Material.REDSTONE_BLOCK, event, 1000));
    }

    public void sell(Material material, InventoryClickEvent event, int amount) {
        Player player = getPlayer();
        if (material == null) return;


        event.setCancelled(true);

        boolean has = player.getInventory().contains(material);

        if (has){
            List<ItemStack> items = new ArrayList<>();

            for (ItemStack item : player.getInventory()) {
                if (item == null) continue;
                if (item.getType() != material) continue;

                items.add(item);
            }

            ItemStack converted = items.get(0);

            if (converted.getAmount() >= 32){
                converted.setAmount(converted.getAmount() - 32);

                FrozenEconomyHandler.setBalance(player.getUniqueId(), FrozenEconomyHandler.getBalance(player.getUniqueId()) + amount);
                player.sendMessage(CC.translate("&aYou have sold &e32 &a" + MobUtil.getItemName(new ItemStack(material)) + " &afor &e" + amount + "&a. Your balance is now &e" + FrozenEconomyHandler.getBalance(player.getUniqueId()) + "&a."));
            } else {
                player.sendMessage(CC.translate("&cYou must have at least &e32 &cblocks of this item to sell it."));
            }
        } else {
            player.sendMessage(CC.translate("&cYou don't have this item in your inventory."));
        }
    }
}

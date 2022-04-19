package net.frozenorb.foxtrot.sell;

import io.github.nosequel.menu.Menu;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.sell.button.MainSellButton;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SellMenu extends Menu {

    public SellMenu(Player player) {
        super(player, "Sell Menu", 9 * 3);
    }

    @Override
    public void tick() {
        this.buttons[2] = new MainSellButton(Material.DIAMOND_BLOCK, "&a&lDiamond Block");
        this.buttons[3] = new MainSellButton(Material.EMERALD_BLOCK, "&a&lEmerald Block");
        this.buttons[4] = new MainSellButton(Material.GOLD_BLOCK, "&e&lGold Block");
        this.buttons[5] = new MainSellButton(Material.IRON_BLOCK, "&7&lIron Block");
        this.buttons[6] = new MainSellButton(Material.REDSTONE_BLOCK, "&c&lRedstone Block");
    }

    public void sell(Material material, InventoryClickEvent event, int amount) {
        Player player = getPlayer();
        if (material == null) return;

        event.setCancelled(true);

        boolean has = player.getInventory().contains(material);

        if (has){
            ItemStack converted = new ItemStack(material);

            if (converted.getAmount() >= 32){
                converted.setAmount(converted.getAmount() - 32);

                FrozenEconomyHandler.setBalance(player.getUniqueId(), FrozenEconomyHandler.getBalance(player.getUniqueId()) + amount);
                player.sendMessage(CC.translate("&aYou have sold &e32 &a" + material.name().toLowerCase() + " &afor &e" + amount + "&a. Your balance is now &e" + FrozenEconomyHandler.getBalance(player.getUniqueId()) + "&a."));
            } else {
                player.sendMessage(CC.translate("&cYou must have at least &e32 blocks of this item to sell it."));
            }
        }
    }
}

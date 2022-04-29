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

public class HalloweenMenu extends Menu {

    public HalloweenMenu(Player player){
        super(player, CC.translate("&6Halloween Blocks"), 9 * 5);

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

        this.buttons[21] = new BuyButton("Pumpkin", 250, 16, Material.PUMPKIN).setClickAction(event -> buy(event, Material.PUMPKIN, 250));
        this.buttons[23] = new BuyButton("Jack o'Lantern", 250, 16, Material.JACK_O_LANTERN).setClickAction(event -> buy(event, Material.JACK_O_LANTERN, 250));

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

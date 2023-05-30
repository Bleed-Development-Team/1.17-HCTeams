package net.frozenorb.foxtrot.gameplay.blockshop.button;

import io.github.nosequel.menu.buttons.Button;
import org.bukkit.Material;

public class BuyButton extends Button {

    String name;
    int price;
    int amount;

    public BuyButton(String name, int price, int amount, Material material) {
        super(material);

        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    @Override
    public String getDisplayName(){
        return "&f" + name;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public String[] getLore() {
        return new String[] {
                "&7&m----------------------------",
                "&fCost&7: &e$" + price,
                "",
                "&7Right click to purchase this item.",
                "&7&m----------------------------"
        };
    }
}

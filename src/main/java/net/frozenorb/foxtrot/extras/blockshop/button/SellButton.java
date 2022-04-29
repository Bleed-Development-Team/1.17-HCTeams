package net.frozenorb.foxtrot.extras.blockshop.button;

import io.github.nosequel.menu.buttons.Button;
import org.bukkit.Material;

public class SellButton extends Button {

    String name;
    int price;
    int sellAmount;
    int amount;

    public SellButton(String name, int sellAmount, int amount, Material material) {
        super(material);

        this.name = name;
        this.sellAmount = sellAmount;
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
                "&fSell&7: &e$" + sellAmount,
                "",
                "&7Right click to sell this item.",
                "&7&m----------------------------"
        };
    }
}

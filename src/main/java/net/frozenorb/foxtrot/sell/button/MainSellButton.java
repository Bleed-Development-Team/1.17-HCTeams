package net.frozenorb.foxtrot.sell.button;

import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;

import java.text.NumberFormat;
import java.util.Locale;

public class MainSellButton extends Button {

    private String name;

    public MainSellButton(Material material, String name){
        super(material);

        this.name = name;
    }

    public String getName(){
        return CC.translate(name);
    }

    @Override
    public String[] getLore() {

        return new String[]{
                "&7Click here to sell this item."
        };
    }
}

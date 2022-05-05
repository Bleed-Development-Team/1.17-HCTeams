package net.frozenorb.foxtrot.extras.sell.button;

import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;

public class MainSellButton extends Button {

    private String name;

    public MainSellButton(Material material, String name){
        super(material);

        this.name = name;
    }

    @Override
    public String getDisplayName(){
        return CC.translate(name);
    }

    @Override
    public int getAmount() {
        return 32;
    }

    @Override
    public String[] getLore() {

        return new String[]{
                "&aClick here to sell this item.",
                "",
                "&eSell Price&7: &f$1000"
        };
    }
}

package net.frozenorb.foxtrot.extras.blockshop.button;

import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;

public class MainButton extends Button {

    /*
    private final String name;
    private final int amount;
    private final double price;
     */

    private final String name;

    public MainButton(String name, Material material) {
        super(material);

        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return CC.translate(name);
    }
}

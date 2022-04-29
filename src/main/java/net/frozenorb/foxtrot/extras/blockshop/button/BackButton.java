package net.frozenorb.foxtrot.extras.blockshop.button;

import io.github.nosequel.menu.buttons.Button;
import org.bukkit.Material;

public class BackButton extends Button {

    public BackButton() {
        super(Material.BARRIER);
    }

    @Override
    public String getDisplayName(){
        return "&cBack";
    }

    @Override
    public String[] getLore() {
        return new String[]{
                "&7&m----------------------------",
                "&7Click to go back.",
                "&7&m----------------------------"
        };
    }
}

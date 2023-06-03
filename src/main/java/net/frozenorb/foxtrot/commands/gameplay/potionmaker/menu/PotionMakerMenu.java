package net.frozenorb.foxtrot.commands.gameplay.potionmaker.menu;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PotionMakerMenu extends Menu {

    public PotionMakerMenu(Player player) {
        super(player, "Potion Maker Recipe", 9);
    }

    @Override
    public void tick() {
        buttons[0] = new Button(Material.GLISTERING_MELON_SLICE);
        buttons[1] = new Button(Material.GLASS);
        buttons[2] = new Button(Material.GLOWSTONE_DUST);
        buttons[3] = new Button(Material.NETHER_WART);
    }
}

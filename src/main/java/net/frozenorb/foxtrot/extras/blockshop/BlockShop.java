package net.frozenorb.foxtrot.extras.blockshop;

import io.github.nosequel.menu.Menu;
import net.frozenorb.foxtrot.extras.blockshop.button.MainButton;
import net.frozenorb.foxtrot.extras.blockshop.impl.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BlockShop extends Menu {


    public BlockShop(Player player) {
        super(player,"Block Shop", 9 * 3);

    }

    @Override
    public void tick() {
        this.buttons[11] = new MainButton("&a&lWool", Material.RED_WOOL).setClickAction(event -> new WoolMenu(getPlayer()).updateMenu());
        this.buttons[12] = new MainButton("&a&lGlass", Material.GRAY_STAINED_GLASS).setClickAction(event -> new GlassMenu(getPlayer()).updateMenu());
        this.buttons[13] = new MainButton("&2&lWood", Material.OAK_WOOD).setClickAction(event -> new WoodMenu(getPlayer()).updateMenu());
        this.buttons[14] =  new MainButton("&a&lOther Blocks", Material.GRASS).setClickAction(event -> new OtherBlocksMenu(getPlayer()).updateMenu());
        this.buttons[15] = new MainButton("&c&lChristmas &2&lBlocks", Material.SNOW_BLOCK).setClickAction(event -> new ChristmasBlocksMenu(getPlayer()).updateMenu());
    }

}

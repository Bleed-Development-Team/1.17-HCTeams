package net.frozenorb.foxtrot.gameplay.guide.menus;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GuideMenu extends Menu {
    public GuideMenu(Player player) {
        super(player, "ㅵ", 54);
    }

    /**
     * The method to get the buttons for the current inventory tick
     * <p>
     * Use {@code this.buttons[index] = Button} to assign
     * a button to a slot.
     */
    @Override
    public void tick() {
        //TODO Lores on these look at orbitzone for ideas
        this.buttons[11] = new Button(ItemBuilder.of(Material.END_PORTAL_FRAME).name(CC.translate("&a&lThe End")).build());
        this.buttons[13] = new Button(ItemBuilder.of(Material.PLAYER_HEAD).name("&b&lFrozen &7| &fHCF").owningPlayer("lolitsalex").build());
        this.buttons[15] = new Button(ItemBuilder.of(Material.OBSIDIAN).name(CC.translate("&c&lThe Nether")).build());

        this.buttons[29] = new Button(ItemBuilder.of(Material.GOLDEN_CHESTPLATE).name(CC.translate("&e&lBard Class")).build());
        this.buttons[31] = new Button(ItemBuilder.of(Material.LEATHER_CHESTPLATE).name(CC.translate("&a&lArcher Class")).build());
        this.buttons[33] = new Button(ItemBuilder.of(Material.CHAINMAIL_CHESTPLATE).name(CC.translate("&8&lRogue Class")).build());

        this.buttons[49] = new Button(ItemBuilder.of(Material.DIAMOND_SWORD).name(CC.translate("&b&lPvP")).build());
    }
}

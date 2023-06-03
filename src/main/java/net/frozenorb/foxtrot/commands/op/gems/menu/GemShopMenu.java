package net.frozenorb.foxtrot.commands.op.gems.menu;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

/**
 * TODO: Write class description here
 *
 * @author Nopox
 * @project 1.17-HCTeams
 * @since 6/2/2023
 */
public class GemShopMenu extends Menu {

    public GemShopMenu(Player player) {
        super(player, CC.translate("&a&lGems Shop"), 27);
    }

    @Override
    public void tick() {
        for (int i = 0; i < 27; i++) {
            if (i >= 10 && i <= 16) continue;

            buttons[i] = new Button(
                    ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build()
            );
        }

        buttons[11] = getCategoryButton(Material.BEACON, "Abilities", true);// COMING SOON
        buttons[13] = getCategoryButton(Material.DIAMOND_CHESTPLATE, "Clickable Kits", false);
        buttons[15] = getCategoryButton(Material.LEVER, "Crate Keys", true);
    }

    public Button getCategoryButton(Material material, String name, boolean comingSoon) {
        return new Button(
                ItemBuilder.of(material)
                        .name("&b&l" + name)
                        .addToLore(" ")
                        .build()
        );
    }
}

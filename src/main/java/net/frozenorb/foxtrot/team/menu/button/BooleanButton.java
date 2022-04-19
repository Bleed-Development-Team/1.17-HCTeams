package net.frozenorb.foxtrot.team.menu.button;

import io.github.nosequel.menu.buttons.Button;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.lib.menu.Button;
import net.frozenorb.foxtrot.util.Callback;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BooleanButton extends Button {

    private boolean accept;
    private Callback<Boolean> callback;

    /**
     * @param material the icon of the button
     */
    public BooleanButton(Boolean status) {
        super(Material.WHITE_WOOL);
        this.accept = status;
    }

    /**
     * Make a new {@link Button} object from an {@link ItemStack}
     *
     * @param itemStack the item stack to get it from
     */
    public BooleanButton() {
        super(ItemBuilder.of(Material.WHITE_WOOL).name(this.accept ? "§aConfirm" : "§cCancel").build());
    }

    public void clicked(Player player, int i, ClickType clickType) {
        if (accept) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 20f, 0.1f);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_GRAVEL_HIT, 20f, 0.1F);
        }
        player.closeInventory();

        callback.callback(accept);
    }

    public String getName(Player player) {
        return accept ? "§aConfirm" : "§cCancel";
    }

    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    public byte getDamageValue(Player player) {
        return accept ? (byte) 5 : (byte) 14;
    }

    public Material getMaterial(Player player) {
        return Material.WHITE_WOOL;
    }

}

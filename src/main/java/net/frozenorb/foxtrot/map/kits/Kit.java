package net.frozenorb.foxtrot.map.kits;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class Kit {

    private final String name;

    private ItemStack icon;

    private ItemStack[] inventoryContents;
    private ItemStack[] armorContents;

    public void apply(Player player) {
        player.getInventory().setContents(inventoryContents);
        player.getInventory().setArmorContents(armorContents);

        player.updateInventory();
    }

    public void update(PlayerInventory inventory) {
        inventoryContents = inventory.getContents();
        armorContents = inventory.getArmorContents();
    }
    
    public Kit clone() {
        Kit kit = new Kit(this.getName());
        kit.setIcon(this.icon);
        kit.setArmorContents(Arrays.copyOf(this.armorContents, this.armorContents.length));
        kit.setInventoryContents(Arrays.copyOf(this.inventoryContents, this.inventoryContents.length));
        return kit;
    }

}

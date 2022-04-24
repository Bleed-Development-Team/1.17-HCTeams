package net.frozenorb.foxtrot.extras.ability.impl.portable;

import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.impl.portable.menu.PortableMenu;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PortableBard extends Ability implements Listener {

    @Override
    public String getName() {
        return "&6&lPortable Bard";
    }

    @Override
    public String getUncoloredName() {
        return "Portable Bard";
    }

    @Override
    public String getDescription() {
        return "Right click to view a menu with different effects.";
    }

    @Override
    public String getCooldownID() {
        return "portable";
    }

    @Override
    public int getCooldown() {
        return 1;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getPortableBard();
    }


    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (!isSimilarTo(player.getItemInHand(), Items.getPortableBard())) return;
            if (!canUse(player)) return;

            new PortableMenu(player).updateMenu();
        }
    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getPortableBard())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&c's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            }
        }
    }

}

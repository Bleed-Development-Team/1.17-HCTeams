package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LuckyModeAbility extends Ability implements Listener {


    @Override
    public String getName() {
        return "&e&lLucky Mode";
    }

    @Override
    public String getUncoloredName() {
        return "Lucky Mode";
    }

    @Override
    public String getDescription() {
        return "Right click to have a chance of either having Strength II or Weakness II.";
    }

    @Override
    public String getCooldownID() {
        return "lucky";
    }

    @Override
    public int getCooldown() {
        return 60 * 2;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getLuckyMode();
    }

    @EventHandler
    public void use(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (!canUse(player)) return;

            int chance = Foxtrot.RANDOM.nextInt(1, 2);

            if (chance == 1){
                
            } else {

            }
        }
    }
}

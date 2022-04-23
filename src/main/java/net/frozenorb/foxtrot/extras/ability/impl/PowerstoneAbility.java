package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PowerstoneAbility extends Ability implements Listener {


    @Override
    public String getName() {
        return "&5&lPowerstone";
    }


    @Override
    public String getUncoloredName(){
        return "Powerstone";
    }

    @Override
    public String getDescription() {
        return "You have received 5 seconds of Strength II, Resistance III, and Speed III.";
    }

    @Override
    public String getCooldownID() {
        return "powerstone";
    }

    @Override
    public int getCooldown() {
        return 60 * 3;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getPowerstone();
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void rightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (isSimilarTo(player.getItemInHand(), Items.getPowerstone())) {
                if (isOnGlobalCooldown(player)){
                    player.sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(player,"partner") + "&c."));
                    //event.setCancelled(true);
                    return;
                }

                if (isOnCooldown(player)){
                    player.sendMessage(CC.translate("&cYou are on cooldown for the &5&lPowerstone &cfor another &c&l" + getCooldownFormatted(player) + "&c."));
                    //event.setCancelled(true);
                    return;
                }


                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2));

                applySelf(player);
            }
        }
    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getPowerstone())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&6's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            } else {
                player.sendMessage(CC.translate("&cYou are not on cooldown for this item."));
            }
        }
    }

}

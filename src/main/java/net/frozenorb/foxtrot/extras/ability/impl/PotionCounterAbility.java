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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PotionCounterAbility extends Ability implements Listener {
    @Override
    public String getName() {
        return "&3&lPotion Counter";
    }

    @Override
    public String getUncoloredName() {
        return "Potion Counter";
    }

    @Override
    public String getDescription() {
        return "Checks how many potions a player has.";
    }

    @Override
    public String getCooldownID() {
        return "potioncounter";
    }

    @Override
    public int getCooldown() {
        return 60 * 2;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getPotionCounter();
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager) || !(event.getEntity() instanceof Player victim)) return;

        if (!isSimilarTo(damager.getItemInHand(), Items.getPotionCounter())) return;
        if (!canUse(damager)) return;
        if (!victimCheck(damager, victim)) return;


        int potions = 0;

        for (ItemStack item : victim.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() == Material.SPLASH_POTION ||item.getType() == Material.POTION) {
                potions++;
            }
        }

        if (damager.getItemInHand().getAmount() > 1) {
            int amount = damager.getItemInHand().getAmount() - 1;
            damager.getItemInHand().setAmount(amount);
        } else {
            damager.setItemInHand(null);
        }

        damager.sendMessage(CC.translate("&f" + victim.getName() + " &6has &f" + potions + " &6potions."));
        giveCooldowns(damager);

    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getPotionCounter())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&c's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            }
        }
    }
}

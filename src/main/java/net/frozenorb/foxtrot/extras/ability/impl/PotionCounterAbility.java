package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
    public Material getMaterial() {
        return Material.STICK;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if (!isSimilarTo(damager.getItemInHand(), Items.getPotionCounter())) return;

        if (isOnGlobalCooldown(damager)){
            damager.sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(damager,"partner") + "&c."));
            return;
        }
        if (isOnCooldown(damager)){
            damager.sendMessage(CC.translate("&cYou are on cooldown for the " + getName() + " &cfor another &c&l" + getCooldownFormatted(damager) + "&c."));
            return;
        }
        int potions = 0;
        for (ItemStack item : victim.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() == Material.SPLASH_POTION ||item.getType() == Material.POTION) {
                potions++;
            }
        }
        damager.sendMessage(CC.translate("&f  " + victim.getName() + " &6has &f" + potions + " &6potions."));
        giveCooldowns(damager);

    }
}

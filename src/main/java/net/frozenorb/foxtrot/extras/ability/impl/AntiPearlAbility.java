package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.server.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.server.event.EnderpearlCooldownAppliedEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class AntiPearlAbility extends Ability implements Listener {
    @Override
    public String getName() {
        return "&3&lAnti-Pearl";
    }

    @Override
    public String getUncoloredName() {
        return "Anti-Pearl";
    }

    @Override
    public String getDescription() {
        return "Puts a player on ender pearl cooldown.";
    }

    @Override
    public String getCooldownID() {
        return "antipearl";
    }

    @Override
    public int getCooldown() {
        return 60 * 2;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getAntiPearl();
    }


    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if (!isSimilarTo(damager.getItemInHand(), Items.getAntiPearl())) return;

        if (isOnGlobalCooldown(damager)) {
            damager.sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(damager, "partner") + "&c."));
            return;
        }
        if (isOnCooldown(damager)) {
            damager.sendMessage(CC.translate("&cYou are on cooldown for the " + getName() + " &cfor another &c&l" + getCooldownFormatted(damager) + "&c."));
            return;
        }

        long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(victim.getLocation()) ? 30_000L : Foxtrot.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1_000L : 16_000L;

        EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(victim, timeToApply);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(appliedEvent);

        EnderpearlCooldownHandler.getEnderpearlCooldown().put(victim.getName(), System.currentTimeMillis() + appliedEvent.getTimeToApply());
        applyOther(damager, victim);

        applyOther(damager, victim);


    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (!isSimilarTo(player.getItemInHand(), Items.getAntiPearl())) return;

            if (isOnCooldown(player)) {
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&6's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            } else {
                player.sendMessage(CC.translate("&cYou are not on cooldown for this item."));
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (!isSimilarTo(player.getInventory().getItemInMainHand(), Items.getAntiPearl())) return;
            if (!isSimilarTo(player.getInventory().getItemInOffHand(), Items.getAntiPearl())) return;

            event.setCancelled(true);

        }
    }
}

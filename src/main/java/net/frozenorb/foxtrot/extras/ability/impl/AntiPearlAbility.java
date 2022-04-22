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
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
    public Material getMaterial() {
        return Material.ENDER_EYE;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if (!isSimilarTo(damager.getItemInHand(), Items.getAntiPearl())) return;

        if (isOnGlobalCooldown(damager)){
            damager.sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(damager,"partner") + "&c."));
            return;
        }
        if (isOnCooldown(damager)) {
            damager.sendMessage(CC.translate("&cYou are on cooldown for the " + getName() + " &cfor another &c&l" + getCooldownFormatted(damager) + "&c."));
            return;
        }

        long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(victim.getLocation()) ? 30_000L : Foxtrot.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1_000L : 16_000L;

        EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(victim, timeToApply);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(appliedEvent);

        EnderpearlCooldownHandler.getEnderpearlCooldown().put(victim.getName(), System.currentTimeMillis() + appliedEvent.getTimeToApply());        applyOther(damager, victim);

        Cooldown.addCooldown("enderpearl", damager, 16);
        applyOther(damager, victim);


    }
}

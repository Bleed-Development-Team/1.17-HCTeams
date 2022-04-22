package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class BackToTheRootsAbility extends Ability implements Listener {
    @Override
    public String getName() {
        return "&f&lBack to the Roots";
    }

    @Override
    public String getUncoloredName() {
        return "Back to the Roots";
    }

    @Override
    public String getDescription() {
        return "Hit a player 3 times to remove hit cooldown for 5 seconds.";
    }

    @Override
    public String getCooldownID() {
        return "backtotheroots";
    }

    @Override
    public int getCooldown() {
        return 60*3;
    }

    @Override
    public Material getMaterial() {
        return Material.BONE_MEAL;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        if (!isSimilarTo(damager.getItemInHand(), Items.getBackToTheRoots())) return;

        if (isOnGlobalCooldown(damager)){
            damager.sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(damager,"partner") + "&c."));
            return;
        }

        if (isOnCooldown(damager)){
            damager.sendMessage(CC.translate("&cYou are on cooldown for &f&lBack to the Roots &cfor another &c&l" + getCooldownFormatted(damager) + "&c."));
            return;
        }

        if (victim.hasMetadata("backtotheroots")) {
            damager.sendMessage(CC.translate("&cThat player is already under the back to the roots effect."));
            return;
        }

        int maxDamageTick = victim.getMaximumNoDamageTicks();

        victim.setNoDamageTicks(0);
        applyOther(damager, victim);

        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            victim.setNoDamageTicks(maxDamageTick);
            victim.sendMessage(CC.translate("&cYou are no longer under the back to the roots effect."));
            victim.removeMetadata("backtotheroots", Foxtrot.getInstance());
        }, 20 * 5);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasMetadata("backtotheroots")) {
            event.getPlayer().removeMetadata("backtotheroots", Foxtrot.getInstance());
        }
    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getBackToTheRoots())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&6's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            } else {
                player.sendMessage(CC.translate("&cYou are not on cooldown for this item."));
            }
        }
    }
}
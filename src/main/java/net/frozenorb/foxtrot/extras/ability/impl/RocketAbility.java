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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RocketAbility extends Ability implements Listener {

    List<UUID> rockets = new ArrayList<>();

    @Override
    public String getName() {
        return "&b&lRocket";
    }

    @Override
    public String getUncoloredName() {
        return "Rocket";
    }

    @Override
    public String getDescription() {
        return "Right click to launch yourself up into the air.";
    }

    @Override
    public String getCooldownID() {
        return "rocket";
    }

    @Override
    public int getCooldown() {
        return 60 * 2;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getRocket();
    }

    @EventHandler
    public void onInteraction(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
            if (!isSimilarTo(player.getItemInHand(), Items.getRocket())) return;

            if (isOnGlobalCooldown(player)){
                player.sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(player,"partner") + "&c."));
                event.setCancelled(true);
                return;
            }

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on cooldown for the &b&lRocket &cfor another &c&l" + getCooldownFormatted(player) + "&c."));
                event.setCancelled(true);
                return;
            }
//TODO ayo this crashes the server
            player.setVelocity(player.getLocation().getDirection().multiply(3));
            applySelf(player);

            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                if (!rockets.contains(player.getUniqueId())) return;

                rockets.remove(player.getUniqueId());
            }, 20L * 15L);
        }
    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getRocket())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&c's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            } else {
                player.sendMessage(CC.translate("&cYou are not on cooldown for this item."));
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR && isOnGlobalCooldown(player) || isOnCooldown(player)){
            //if (!isSimilarTo(player.getInventory().getItemInMainHand(), Items.getRocket())) return;
            //if (!isSimilarTo(player.getInventory().getItemInOffHand(), Items.getRocket())) return;
            //I think its already above but ima leave it here for testing yk
            //event.setCancelled(true);

        }
    }

    @EventHandler
    public void damage(EntityDamageEvent event){
        Player victim = (Player) event.getEntity();

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL){
            if (rockets.contains(victim.getUniqueId())){
                event.setCancelled(true);

                rockets.remove(victim.getUniqueId());
            }
        }
    }
}

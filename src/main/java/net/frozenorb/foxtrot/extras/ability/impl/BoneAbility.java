package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoneAbility extends Ability implements Listener {

    Map<UUID, Map<UUID, Integer>> hitMap = new HashMap<>();

    @Override
    public String getName() {
        return "&6&lAnti-Build Stick";
    }

    @Override
    public String getUncoloredName(){
        return "Anti-Build Stick";
    }

    @Override
    public String getDescription() {
        return "You have disallowed that player to place, break, and interact with blocks.";
    }

    @Override
    public String getCooldownID() {
        return "bone";
    }

    @Override
    public int getCooldown() {
        return 120;
    }


    @Override
    public ItemStack getItemStack() {
        return Items.getBoneAbility();
    }

    @EventHandler(ignoreCancelled = true)
    public void hit(EntityDamageByEntityEvent event){
        if (event.isCancelled()) return;
        Player victim = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
        Player damager = event.getDamager() instanceof Player ? (Player) event.getDamager() : null;

        if (damager == null || victim == null) return;

        if (!isSimilarTo(damager.getItemInHand(), Items.getBoneAbility())) return;

        if (isOnGlobalCooldown(damager)){
            damager.sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(damager,"partner") + "&c."));
            return;
        }

        if (isOnCooldown(damager)){
            damager.sendMessage(CC.translate("&cYou are on cooldown for the &6&lAnti-Build Stick &cfor another &c&l" + getCooldownFormatted(damager) + "&c."));
            return;
        }

        int hits;

        if (hitMap.containsKey(damager.getUniqueId()) && hitMap.get(damager.getUniqueId()).containsKey(victim.getUniqueId())) {
            hits = hitMap.get(damager.getUniqueId()).get(victim.getUniqueId());
        } else {
            hits = 1;
        }

        if (hits < 3){
            Map<UUID, Integer> victimMap = new HashMap<>();
            victimMap.put(victim.getUniqueId(), hits + 1);
            hitMap.put(damager.getUniqueId(), victimMap);

            damager.sendMessage(CC.translate("&6You need to hit &f" + victim.getName() + " &r&6" + (3 - hits) + " &6more time" + (3 - hits == 1 ? "" : "s") + "."));
            return;
        }

        hitMap.remove(damager.getUniqueId());

        Cooldown.addCooldown("bone-eff", damager, 15);

        if (damager.getItemInHand().getAmount() > 1) {
            int amount = damager.getItemInHand().getAmount() - 1;
            damager.getItemInHand().setAmount(amount);
        } else {
            damager.setItemInHand(null);
        }


        applyOther(damager, victim);
    }

    @EventHandler
    public void place(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if (Cooldown.isOnCooldown("bone-eff", player)){
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou cannot place blocks for another &c&l" + Cooldown.getCooldownString(player, "bone-eff") + "&c."));
        }
    }

    @EventHandler
    public void breakb(BlockBreakEvent event){
        Player player = event.getPlayer();

        if (Cooldown.isOnCooldown("bone-eff", player)){
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou cannot block blocks for another &c&l" + Cooldown.getCooldownString(player, "bone-eff") + "&c."));
        }
    }

    @EventHandler
    public void inte(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction().name().contains("RIGHT") && !event.getAction().name().contains("AIR")) {
           if (event.getClickedBlock().getType() == Material.OAK_FENCE_GATE || event.getClickedBlock().getType() == Material.SPRUCE_FENCE_GATE || event.getClickedBlock().getType() == Material.BIRCH_FENCE_GATE || event.getClickedBlock().getType() == Material.JUNGLE_FENCE_GATE || event.getClickedBlock().getType() == Material.DARK_OAK_FENCE_GATE || event.getClickedBlock().getType() == Material.ACACIA_FENCE_GATE || event.getClickedBlock().getType() == Material.OAK_DOOR) {
               if (Cooldown.isOnCooldown("bone-eff", player)){
                   event.setCancelled(true);
                   player.sendMessage(CC.translate("&cYou cannot interact with blocks for another &c&l" + Cooldown.getCooldownString(player, "bone-eff") + "&c."));
               }
           }
        }
    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getBoneAbility())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&6's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            }
        }
    }
}

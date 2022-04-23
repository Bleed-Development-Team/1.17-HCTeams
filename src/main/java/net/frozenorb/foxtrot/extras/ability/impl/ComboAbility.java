package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ComboAbility extends Ability implements Listener {

    Map<UUID, Integer> combos = new HashMap<>();

    @Override
    public String getName() {
        return "&6&lCombo Ability";
    }

    @Override
    public String getUncoloredName() {
        return "Combo Ability";
    }

    @Override
    public String getDescription() {
        return "Receive a second for Strength II for every hit.";
    }

    @Override
    public String getCooldownID() {
        return "combo";
    }

    @Override
    public int getCooldown() {
        return 60 * 3;
    }


    @Override
    public ItemStack getItemStack() {
        return Items.getComboAbility();
    }

    @EventHandler
    public void rightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (isSimilarTo(player.getItemInHand(), Items.getComboAbility())){
                if (!canUse(player)) return;

                combos.put(player.getUniqueId(), combos.getOrDefault(player.getUniqueId(), 0) + 1);
                Cooldown.addCooldown("combo-eff", player, 10);

                applySelf(player);

                Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (20 * combos.get(player.getUniqueId())), 1));
                    player.sendMessage(CC.translate("&6You have received &f" + combos.get(player.getUniqueId()) + " &6second" + (combos.get(player.getUniqueId()) == 1 ? "" : "s") + " of Strength II."));

                    combos.remove(player.getUniqueId());
                }, 20L * 10L);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void hitPlayer(EntityDamageByEntityEvent event){
        if (!(event.getEntity() instanceof Player) || (!(event.getDamager() instanceof Player))) return;

        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (Cooldown.isOnCooldown("combo-eff", damager)){
            if (combos.containsKey(damager.getUniqueId()) && combos.get(damager.getUniqueId()) < 10){
                if (DTRBitmask.SAFE_ZONE.appliesAt(victim.getLocation())) return;
                if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(victim.getUniqueId())) return;

                combos.put(damager.getUniqueId(), combos.get(damager.getUniqueId()) + 1);
            }
        }
    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getComboAbility())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&c's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            }
        }
    }
}

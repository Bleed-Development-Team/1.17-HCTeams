package net.frozenorb.foxtrot.gameplay.ability.damage;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.ItemUtils;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DamageAbilityListener implements Listener {

    @EventHandler
    public void damage(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player damager) || !(event.getEntity() instanceof Player victim)) return;

        DamageAbility ability = HCF.getInstance().getAbilityHandler().getDamageAbility(damager.getItemInHand());
        if (ability == null) return;
        if (!ItemUtils.isSimilarTo(ability.getItemStack(), damager.getItemInHand())) return;

        if (Cooldown.isOnCooldown(ability.getID(), damager)){
            damager.sendMessage(CC.translate("&cYou are on cooldown for the " + ability.getName() + " &r&cfor another &c&l" + TimeUtils.formatIntoDetailedString(Cooldown.getCooldownForPlayerInt(ability.getID(), damager)) + "&c."));
            return;
        }

        if (!ability.canUse(damager)){
            event.setCancelled(true);
            return;
        }
        if (!ability.victimCheck(damager, victim)){
            return;
        }

        ability.handle(event, damager, victim);
    }

}

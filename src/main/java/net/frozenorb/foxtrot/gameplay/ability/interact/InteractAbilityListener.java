package net.frozenorb.foxtrot.gameplay.ability.interact;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.ItemUtils;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractAbilityListener implements Listener {

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        InteractAbility ability = HCF.getInstance().getAbilityHandler().getInteractAbility(player.getItemInHand());
        if (ability == null) return;

        if (!ItemUtils.isSimilarTo(ability.getItemStack(), player.getItemInHand())) return;

        if (Cooldown.isOnCooldown(ability.getID(), player)){
            player.sendMessage(CC.translate("&cYou are on cooldown for the " + ability.getName() + " &r&cfor another &c&l" + TimeUtils.formatIntoDetailedString(Cooldown.getCooldownForPlayerInt(ability.getID(), player)) + "&c."));
            event.setCancelled(true);
            return;
        }

        if (!ability.canUse(player)){
            event.setCancelled(true);
            return;
        }

        ability.handle(event, player);
    }

    /*
    @EventHandler
    public void playerHit(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;


    }

     */

}

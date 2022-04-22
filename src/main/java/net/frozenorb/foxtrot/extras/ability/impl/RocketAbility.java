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
import org.bukkit.event.player.PlayerInteractEvent;

public class RocketAbility extends Ability implements Listener {

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
    public Material getMaterial() {
        return Material.FIREWORK_ROCKET;
    }

    @EventHandler
    public void onInteraction(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
            if (!isSimilarTo(player.getItemInHand(), Items.getRocket())) return;

            if (isOnGlobalCooldown(player)){
                player.sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(player,"partner") + "&c."));
                return;
            }

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on cooldown for the &c&lRocket &cfor another &c&l" + getCooldownFormatted(player) + "&c."));
                return;
            }

            player.setVelocity(player.getLocation().getDirection().multiply(2));
            applySelf(player);
        }
    }
}

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MedkitAbility extends Ability implements Listener {

    @Override
    public String getName() {
        return "&6&lMedKit";
    }

    @Override
    public String getUncoloredName() {
        return "MedKit";
    }

    @Override
    public String getDescription() {
        return "You have received Resistance III & Regeneration III for 4 seconds.";
    }

    @Override
    public String getCooldownID() {
        return "medkit";
    }

    @Override
    public int getCooldown() {
        return 60 * 2;
    }

    @Override
    public Material getMaterial() {
        return Material.PAPER;
    }

    @EventHandler(ignoreCancelled = true)
    public void interaction(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!isSimilarTo(player.getItemInHand(), Items.getMedkit())) return;

            if (isOnGlobalCooldown(player)){
                player.sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(player,"partner") + "&c."));
                return;
            }

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on cooldown for the &6&MedKit &cfor another &c&l" + getCooldownFormatted(player) + "&c."));
                return;
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 4, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 4, 2));

            applySelf(player);
        }
    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getMedkit())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&6's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            } else {
                player.sendMessage(CC.translate("&cYou are not on cooldown for this item."));
            }
        }
    }
}

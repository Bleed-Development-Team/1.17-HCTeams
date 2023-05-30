package net.frozenorb.foxtrot.server.listener.impl;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.server.event.DisallowedPotionDrinkEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;

public class PotionLimiterListener implements Listener {

    @EventHandler
    public void onPotionDrinkEvent(PlayerItemConsumeEvent event) {
        if (event.getItem().isSimilar(InventoryUtils.ANTIDOTE)) {
            Player player = event.getPlayer();

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.removePotionEffect(PotionEffectType.SLOW);
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    player.removePotionEffect(PotionEffectType.POISON);
                    player.removePotionEffect(PotionEffectType.WEAKNESS);
                }
            }.runTaskLater(HCF.getInstance(), 2L);
        }
    }

    /*
    @EventHandler(priority=EventPriority.HIGH)
    public void onPotionSplash(PotionSplashEvent event) {
        for (LivingEntity livingEntity : event.getAffectedEntities()) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(livingEntity.getLocation())) {
                event.setIntensity(livingEntity, 0D);
            }
        }
        PotionMeta potion = (PotionMeta) event.getPotion().getItem().getItemMeta();
        PotionType type = potion.getBasePotionData().getType();
        if (type == PotionType.INSTANT_DAMAGE || type == PotionType.STRENGTH || type == PotionType.WEAKNESS || type == PotionType.AWKWARD || type == PotionType.INVISIBILITY) {
            event.setCancelled(true);
            return;
        }
        //Potion potion = Potion.fromItemStack(event.getPotion().getItem());
        if (!HCF.getInstance().getMapHandler().isKitMap() && !HCF.getInstance().getServerHandler().isVeltKitMap()) {
            if (!HCF.getInstance().getServerHandler().isDrinkablePotionAllowed(potion.getBasePotionData().getType())
                    || !HCF.getInstance().getServerHandler().isPotionLevelAllowed(potion.getBasePotionData().getType(), potion.getCustomEffects().get(0).getAmplifier())) {//TODO its supposed to be potion.getLevel() but I dont see that so please look into that
                event.setCancelled(true);
            } else if (potion.getBasePotionData().isExtended() && (potion.getBasePotionData().getType() == PotionType.SLOWNESS || potion.getBasePotionData().getType() == PotionType.POISON)) {
                event.setCancelled(true);
            } else if (potion.getBasePotionData().getType() == PotionType.POISON && potion.getBasePotionData().isUpgraded()) {
                event.setCancelled(true);
            }
        }
    }

     */

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) {
            return;
        }

        //Potion potion = Potion.fromItemStack(event.getItem());
        if (!(event.getItem().getItemMeta() instanceof PotionMeta potion)) return;


        if (!HCF.getInstance().getServerHandler().isDrinkablePotionAllowed(potion.getBasePotionData().getType()) || !HCF.getInstance().getServerHandler().isPotionLevelAllowed(potion.getBasePotionData().getType(), potion.getCustomEffects().get(0).getAmplifier())) {//TODO look into how to get the level ofi t
            DisallowedPotionDrinkEvent potionDrinkEvent = new DisallowedPotionDrinkEvent(event.getPlayer(), potion);

            if (!potionDrinkEvent.isAllowed()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "This potion is not usable!");
            }
        }
    }

}

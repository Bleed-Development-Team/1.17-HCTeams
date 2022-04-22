package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.server.event.DisallowedPotionDrinkEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

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
            }.runTaskLater(Foxtrot.getInstance(), 2L);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPotionSplash(PotionSplashEvent event) {
        for (LivingEntity livingEntity : event.getAffectedEntities()) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(livingEntity.getLocation())) {
                event.setIntensity(livingEntity, 0D);
            }
        }
        PotionMeta potion = (PotionMeta) event.getPotion().getItem().getItemMeta();
        //Potion potion = Potion.fromItemStack(event.getPotion().getItem());
        if (!Foxtrot.getInstance().getMapHandler().isKitMap() && !Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
            if (!Foxtrot.getInstance().getServerHandler().isDrinkablePotionAllowed(potion.getBasePotionData().getType()) || !Foxtrot.getInstance().getServerHandler().isPotionLevelAllowed(potion.getBasePotionData().getType(), potion.getBasePotionData().hashCode())) {//TODO its supposed to be potion.getLevel() but I dont see that so please look into that
                event.setCancelled(true);
            } else if (potion.getBasePotionData().isExtended() && (potion.getBasePotionData().getType() == PotionType.SLOWNESS || potion.getBasePotionData().getType() == PotionType.POISON)) {
                event.setCancelled(true);
            } else if (potion.getBasePotionData().getType() == PotionType.POISON && potion.getBasePotionData().isUpgraded()) {
                event.setCancelled(true);
            }
        }
        if (potion.getBasePotionData().getType() == PotionType.INSTANT_DAMAGE) {
            event.setCancelled(true);
            return;
        } else if (potion.getBasePotionData().getType() == PotionType.STRENGTH) {
            event.setCancelled(true);
            return;
        }

        if (event.getPotion().getShooter() instanceof Player && !event.isCancelled()) {
            Iterator<PotionEffect> iterator = event.getPotion().getEffects().iterator();

            if (iterator.hasNext()) {
                if (FoxListener.DEBUFFS.contains(iterator.next().getType())) {
                    if (event.getAffectedEntities().size() > 1 || (event.getAffectedEntities().size() == 1 && !event.getAffectedEntities().contains(event.getPotion().getShooter()))) {
                        SpawnTagHandler.addOffensiveSeconds((Player) event.getPotion().getShooter(), SpawnTagHandler.getMaxTagTime());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION || event.getItem().getDurability() == 0) {
            return;
        }

        //Potion potion = Potion.fromItemStack(event.getItem());
        if (!(event.getItem().getItemMeta() instanceof PotionMeta potion)) return;

        if (!Foxtrot.getInstance().getServerHandler().isDrinkablePotionAllowed(potion.getBasePotionData().getType()) || !Foxtrot.getInstance().getServerHandler().isPotionLevelAllowed(potion.getBasePotionData().getType(), potion.getBasePotionData().hashCode())) {//TODO look into how to get the level ofi t
            DisallowedPotionDrinkEvent potionDrinkEvent = new DisallowedPotionDrinkEvent(event.getPlayer(), potion);

            if (!potionDrinkEvent.isAllowed()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "This potion is not usable!");
            }
        }
    }

}

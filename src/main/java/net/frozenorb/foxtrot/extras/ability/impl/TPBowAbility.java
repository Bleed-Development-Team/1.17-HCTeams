package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TPBowAbility extends Ability implements Listener {
    @Override
    public String getName() {
        return "&6&lTeleportation Bow";
    }

    @Override
    public String getUncoloredName() {
        return "Teleportation Bow";
    }

    @Override
    public String getDescription() {
        return "Hit someone with this arrow to teleport to their location.";
    }

    @Override
    public String getCooldownID() {
        return "tpbow";
    }

    @Override
    public int getCooldown() {
        return 60 * 2;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getTPBow() ;
    }

    @EventHandler
    public void entity(EntityDamageByEntityEvent event){
        if (!(event.getEntity() instanceof Player victim)) return;

        if (event.getDamager() instanceof Arrow arrow){
            if (arrow.getShooter() instanceof Player shooter){

                giveCooldowns(shooter);

                new BukkitRunnable(){
                    int i = 0;

                    @Override
                    public void run() {
                        if (i == 0){
                            shooter.sendMessage(CC.translate("&6You are teleporting to &f" + victim.getName() + " &6in &f3 &6seconds."));
                        } else if (i == 1){
                            shooter.sendMessage(CC.translate("&6You are teleporting to &f" + victim.getName() + " &6in &f2 &6seconds."));
                        } else if (i == 2){
                            shooter.sendMessage(CC.translate("&6You are teleporting to &f" + victim.getName() + " &6in &f1 &6second."));
                        } else if (i == 3){
                            shooter.teleport(victim.getLocation());

                            cancel();
                            return;
                        }

                        i++;
                    }
                }.runTaskTimer(Foxtrot.getInstance(), 0L, 20L);
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Player player = event.getPlayer();

            if (!isSimilarTo(player.getItemInHand(), Items.getTPBow())) return;

            if (!canUse(player)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getTPBow())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&c's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            }
        }
    }


}

package net.frozenorb.foxtrot.extras.ability.impl.portable;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PortableBard extends Ability implements Listener {

    List<PotionEffect> effects = new ArrayList<>();
    int seconds;

    @Override
    public String getName() {
        return "&6&lPortable Bard";
    }

    @Override
    public String getUncoloredName() {
        return "Portable Bard";
    }

    @Override
    public String getDescription() {
        return "Right click to view a menu with different effects.";
    }

    @Override
    public String getCooldownID() {
        return "portable";
    }

    @Override
    public int getCooldown() {
        return 1;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getPortableBard();
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (!isSimilarTo(player.getItemInHand(), Items.getPortableBard())) return;
            if (!canUse(player)) {
                event.setCancelled(true);
                return;
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
            }

            effects.clear();
            effects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 1));
            effects.add(new PotionEffect(PotionEffectType.REGENERATION, 80, 2));
            effects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 2));

            Entity entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.SKELETON);
            Skeleton skeleton = (Skeleton) entity;
            skeleton.setAI(false);
            skeleton.setHealth(40);

            entity.setCustomName(CC.translate("&6&l" + player.getName() + "'s Portable Bard &c" + (Math.round(skeleton.getHealth() / 2)) + " ❤"));
            entity.setCustomNameVisible(true);



            if (Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()) != null) {
                entity.setMetadata(Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()).getName(), new FixedMetadataValue(Foxtrot.getInstance(), true));
            }

            useItem(player);
            giveCooldowns(player);

            seconds = 30;

            new BukkitRunnable(){

                final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

                @Override
                public void run() {
                    seconds -= 4;

                    if (entity.isDead() || seconds <= 0){
                        cancel();
                        if (!entity.isDead()){
                            entity.remove();
                        }
                        return;
                    }

                    if (team != null) {
                        for (Player online : team.getNearbyTeamMembers(entity)) {
                            online.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0));
                            online.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 0));
                            online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 0));

                            PotionEffect effect = effects.get(Foxtrot.RANDOM.nextInt(0, effects.size()));
                            online.addPotionEffect(effect);
                        }
                    } else {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 0));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 0));

                        PotionEffect effect = effects.get(Foxtrot.RANDOM.nextInt(0, effects.size()));
                        player.addPotionEffect(effect);
                    }
                }
            }.runTaskTimer(Foxtrot.getInstance(), 0, 80);
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event){
        if (!(event.getEntity() instanceof final Skeleton victim) || !(event.getDamager() instanceof final Player damager)) return;

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(damager);
        if (team == null) return;

        if (victim.hasMetadata(team.getName())){
            event.setCancelled(true);
            damager.sendMessage(CC.translate("&cYou cannot attack your team's portable bard!"));
        }
    }

    @EventHandler
    public void burn(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Skeleton skeleton)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE){
            event.setCancelled(true);
            skeleton.setFireTicks(0);
        }
    }


    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getPortableBard())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&c's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            }
        }
    }

}

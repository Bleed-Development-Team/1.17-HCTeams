package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BallOfRage extends InteractAbility {


    @Override
    public String getID() {
        return "ballofrage";
    }

    @Override
    public String getName() {
        return "&4&lBall of Rage";
    }

    @Override
    public int getCooldown() {
        return 60 * 3;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.EGG)
                .name(getName())
                .addToLore("&7Throw to create a cloud of effects", "&7where all teammates within 5 block radius", "&7will be given positive effects.")
                .build();
    }

    @Override
    public String getColor() {
        return "&4";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {} // already done

    @EventHandler
    public void launch(ProjectileLaunchEvent event){
        if (!(event.getEntity() instanceof Egg) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        event.getEntity().setMetadata("ballofrage", new FixedMetadataValue(HCF.getInstance(), player.getUniqueId().toString()));
        addCooldown(player);
    }

    @EventHandler
    public void land(ProjectileHitEvent event){
        if (!(event.getEntity() instanceof Egg) || !(event.getEntity().getShooter() instanceof Player) || !event.getEntity().hasMetadata("ballofrage")) {
            return;
        }

        Projectile egg = event.getEntity();
        final Player player = (Player) egg.getShooter();

        egg.getWorld().createExplosion(egg.getLocation(), 0);
        egg.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, egg.getLocation(), 1, 0, 0, 0, 0);

        Team team = HCF.getInstance().getTeamHandler().getTeam(player);

        egg.getNearbyEntities(6, 6, 6).stream().filter(it -> it instanceof Player).map(it -> (Player) it).forEach(it -> {
            if (team != null && !team.isMember(it.getUniqueId())) {
                return;
            }

            if (team == null && !it.getName().equalsIgnoreCase(player.getName())) {
                return;
            }

            it.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*6, 1));
            it.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*6, 2));

            it.sendMessage(CC.translate(""));
            it.sendMessage(CC.translate("&4&lBall of Rage"));
            it.sendMessage(CC.translate("&4| &fYou were hit by the &4&lBall of Rage&f!"));
            it.sendMessage(CC.translate("&4| &fYou were given Strength II and Resistance III for 5 seconds!"));
            it.sendMessage(CC.translate(""));
        });
    }
}

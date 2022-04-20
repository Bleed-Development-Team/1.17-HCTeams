package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.extras.ability.item.Items;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoneAbility implements Listener {

    Map<UUID, Integer> hits = new HashMap<>();

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event) {
        Player victim = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
        Player damager = event.getDamager() instanceof Player ? (Player) event.getDamager() : null;

        if (victim != null && damager != null) {
            if (damager.getItemInHand() == Items.getBone()){
                if (Cooldown.isOnCooldown("bone", damager)) {
                    damager.sendMessage(CC.translate("&cYou cannot use this for another &c&l" + Cooldown.getCooldownString(damager, "bone") + "&c!"));
                    event.setCancelled(true);
                    return;
                }

                if (Cooldown.isOnCooldown("boneAffected", victim)){
                    damager.sendMessage(CC.translate("&cThat player is already under the bone effect!"));
                    event.setCancelled(true);
                    return;
                }

                hits.putIfAbsent(victim.getUniqueId(), 1);

                if (hits.get(victim.getUniqueId()) >= 3){
                    victim.sendMessage(CC.translate(""));
                    victim.sendMessage(CC.translate("&c❤ &cYou have been hit with the &fExotic Bone&c!"));
                    victim.sendMessage(CC.translate("&c❤ &cYou cannot break or interact with openables for &f15 seconds&c."));
                    victim.sendMessage(CC.translate(""));

                    damager.sendMessage(CC.translate("&aYou have put &f" + victim.getName() + " &aunder the bone effect!"));

                    Cooldown.addCooldown("bone", damager, 120);
                    Cooldown.addCooldown("boneAffected", victim, 15);

                    hits.remove(victim.getUniqueId());

                    return;
                }

                hits.put(victim.getUniqueId(), hits.get(victim.getUniqueId()) + 1);

                damager.sendMessage(CC.translate("&aYou must hit that player &e" + hits.get(victim.getUniqueId()) + " &amore times to bone them!"));
            }
        }
    }

    @EventHandler
    public void place(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (Cooldown.isOnCooldown("boneAffected", player)) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou must wait &f" + Cooldown.getCooldownString(player, "boneAffected") + " &cto place blocks!"));
        }
    }

    @EventHandler
    public void block(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (Cooldown.isOnCooldown("boneAffected", player)) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou must wait &f" + Cooldown.getCooldownString(player, "boneAffected") + " &cto break blocks!"));
        }
    }

    @EventHandler
    public void place(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() != null){
            if (event.getClickedBlock().getType() == Material.OAK_DOOR || event.getClickedBlock().getType() == Material.OAK_FENCE_GATE){
                if (Cooldown.isOnCooldown("boneAffected", player)) {
                    //event.setCancelled(true);
                    player.sendMessage(CC.translate("&cYou must wait &f" + Cooldown.getCooldownString(player, "boneAffected") + " &cto place blocks!"));
                }
            }
        }
    }
}

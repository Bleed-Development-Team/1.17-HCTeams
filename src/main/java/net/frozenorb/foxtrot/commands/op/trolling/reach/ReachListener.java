package net.frozenorb.foxtrot.commands.op.trolling.reach;


import net.frozenorb.foxtrot.util.DamageUtil;
import net.frozenorb.foxtrot.util.EntityUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ReachListener implements Listener {
    public static HashMap<UUID, Double> reach = new HashMap<>();

    @EventHandler
    public void reachListener(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Double range = reach.get(player.getUniqueId());
        if (range != null) {
            Entity target = EntityUtil.getNearestEntityToPlayer(player, range);
            if (target != null) {
                Double damage = DamageUtil.calculateDamage(player, target);

                if (target instanceof LivingEntity entityTarget) {
                    entityTarget.damage(damage, player);
                }
            }
        }
    }
}

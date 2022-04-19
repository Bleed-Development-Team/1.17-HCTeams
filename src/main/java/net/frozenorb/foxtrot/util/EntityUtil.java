package net.frozenorb.foxtrot.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class EntityUtil {

    public static Entity getNearestEntityToPlayer(Player player, double range) {
        Vector direction = player.getEyeLocation().getDirection();
        List<Entity> entities = player.getNearbyEntities(range, range, range);

        for (Entity entity : entities) {
            Vector angle = entity.getLocation().subtract(player.getLocation()).toVector().normalize();

            if (direction.distance(angle) < 0.1) {
                return entity;
            }
        }
        return null;
    }
}

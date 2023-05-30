package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Rocket extends InteractAbility {
    List<UUID> rockets = new ArrayList<>();

    @Override
    public String getID() {
        return "rocket";
    }

    @Override
    public String getName() {
        return "&c&lRocket";
    }

    @Override
    public int getCooldown() {
        return 120;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.FIREWORK_ROCKET)
                .name("&c&lRocket")
                .addToLore("&7Right click to launch yourself up into the air.")
                .build();
    }

    @Override
    public String getColor() {
        return "&c";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {

        player.setVelocity(player.getLocation().getDirection().multiply(3));

        use(player, "");

        if (event.getAction() == Action.RIGHT_CLICK_AIR){
            useItem(player);
        }

        rockets.add(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            if (!rockets.contains(player.getUniqueId())) return;

            rockets.remove(player.getUniqueId());
        }, 20L * 15L);
    }

    @EventHandler
    public void damage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player victim)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL){
            if (rockets.contains(victim.getUniqueId())){
                event.setCancelled(true);

                rockets.remove(victim.getUniqueId());
            }
        }
    }
}

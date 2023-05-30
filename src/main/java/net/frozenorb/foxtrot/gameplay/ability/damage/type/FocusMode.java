package net.frozenorb.foxtrot.gameplay.ability.damage.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.damage.DamageAbility;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FocusMode extends DamageAbility {

    Map<UUID, Map<UUID, Integer>> hitMap = new HashMap<>();
    Map<UUID, UUID> focusMode = new HashMap<>();

    @Override
    public String getID() {
        return "focus-mode";
    }

    @Override
    public String getName() {
        return "&6&lFocus Mode";
    }

    @Override
    public int getCooldown() {
        return 120;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.GOLD_NUGGET)
                .name(getName())
                .addToLore("&7Hit someone 3 times to deal 20%", "&7more damage for 10 seconds.")
                .build();
    }

    @Override
    public String getColor() {
        return "&6";
    }

    @Override
    public void handle(EntityDamageByEntityEvent event, Player damager, Player victim) {
        int hits;

        if (hitMap.containsKey(damager.getUniqueId()) && hitMap.get(damager.getUniqueId()).containsKey(victim.getUniqueId())) {
            hits = hitMap.get(damager.getUniqueId()).get(victim.getUniqueId());
        } else {
            hits = 1;
        }

        if (hits < 3){
            Map<UUID, Integer> victimMap = new HashMap<>();
            victimMap.put(victim.getUniqueId(), hits + 1);
            hitMap.put(damager.getUniqueId(), victimMap);

            damager.sendMessage(CC.translate("&fYou need to hit &b" + victim.getName() + " &r&f" + (3 - hits) + " &fmore time" + (3 - hits == 1 ? "" : "s") + "."));
            return;
        }

        hitMap.remove(damager.getUniqueId());

        use(damager);

        focusMode.put(damager.getUniqueId(), victim.getUniqueId());

        sendMessage(damager, "&fYou are now dealing 20% more damage to &e" + victim.getName() + "&f.");

        victim.sendMessage(CC.translate("&6" + victim.getName() + " &fhas used a focus mode against you and is now dealing &e20% &fmore damage!"));

        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            focusMode.remove(damager.getUniqueId());
            focusMode.remove(victim.getUniqueId());

            damager.sendMessage(CC.translate("&f"));
            damager.sendMessage(CC.translate("&6&lFocus Mode"));
            damager.sendMessage(CC.translate("&6| &fYour focus mode has expired!"));
            damager.sendMessage(CC.translate("&6| &fCooldown: &6" + TimeUtils.formatIntoDetailedString(this.getCooldown())));
            damager.sendMessage(CC.translate(""));
        }, 10L * 20);
    }

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player damager) || !(event.getEntity() instanceof Player victim)) return;

        if (focusMode.containsKey(event.getDamager().getUniqueId()) && focusMode.containsValue(event.getEntity().getUniqueId())){
            double baseDamage = event.getDamage();
            double focusModeDamage = baseDamage * 1.15; // 10%

            event.setDamage(focusModeDamage);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            event.setCancelled(true); // Cancel the event to prevent hatching
        }
    }

}

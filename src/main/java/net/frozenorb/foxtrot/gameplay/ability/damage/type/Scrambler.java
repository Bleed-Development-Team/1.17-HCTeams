package net.frozenorb.foxtrot.gameplay.ability.damage.type;

import net.frozenorb.foxtrot.gameplay.ability.damage.DamageAbility;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Scrambler extends DamageAbility {
    Map<UUID, Map<UUID, Integer>> hitMap = new HashMap<>();

    @Override
    public String getID() {
        return "Scrambler";
    }

    @Override
    public String getName() {
        return "&9&lScrambler";
    }

    @Override
    public int getCooldown() {
        return 60 * 3;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.TROPICAL_FISH)
                .name(getName())
                .addToLore("&7Hit a player 3 times to scramble their hotbar.")
                .build();
    }

    @Override
    public String getColor() {
        return "&9";
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

            damager.sendMessage(CC.translate("&fYou need to hit " + getColor() + victim.getName() + " &r&f" + (3 - hits) + " &fmore time" + (3 - hits == 1 ? "" : "s") + "."));
            return;
        }

        hitMap.remove(damager.getUniqueId());

        List<ItemStack> hi = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            hi.add(victim.getInventory().getItem(i));
        }

        Collections.shuffle(hi);

        for (int i = 0; i < 9; i++) {
            victim.getInventory().setItem(i, hi.get(i));
        }

        use(damager);
        sendMessage(damager, "You have scrambled " + getColor() + victim.getName() + "&f's hotbar.");

        victim.sendMessage(CC.translate(""));
        victim.sendMessage(CC.translate("&4" + damager.getName() + " &chas hit you with a scrambler!"));
        victim.sendMessage(CC.translate("&cYour hotbar has been scrambled."));
        victim.sendMessage(CC.translate(""));
    }
}

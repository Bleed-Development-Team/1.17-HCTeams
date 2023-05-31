package net.frozenorb.foxtrot.gameplay.ability.damage.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.damage.DamageAbility;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class EffectStealer extends DamageAbility {
    Map<UUID, Map<UUID, Integer>> hitMap = new HashMap<>();

    @Override
    public String getID() {
        return "effectstealer";
    }

    @Override
    public String getName() {
        return "&c&lEffect Stealer";
    }

    @Override
    public int getCooldown() {
        return 60 * 3;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.NETHER_BRICK)
                .name(getName())
                .addToLore("&7Hit someone 3 times to take their effects away and give yourself them.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public String getColor() {
        return "&c";
    }

    @Override
    public void handle(EntityDamageByEntityEvent event, Player damager, Player victim) {
        if (!isWearingSet(victim.getInventory())) {
            damager.sendMessage(CC.translate("&cYou cannot steal a kit's effects."));
            return;
        }


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

        Collection<PotionEffect> potionEffects = victim.getActivePotionEffects();

        for (PotionEffect potionEffect : potionEffects) {
            victim.removePotionEffect(potionEffect.getType());
            if (!damager.hasPotionEffect(potionEffect.getType())){
                damager.addPotionEffect(potionEffect);
            }
        }

        victim.sendMessage(CC.translate("&cAll your effects have been stolen!"));
        sendMessage(damager, "You have stolen all &c" + victim.getName() + "&f's effects.");

        use(damager);

        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            for (PotionEffect potionEffect : potionEffects) {
                if (victim.hasPotionEffect(potionEffect.getType())) continue;

                victim.addPotionEffect(potionEffect);
            }
            victim.sendMessage(CC.translate("&aYou have received all your effects back!"));
        }, 20 * 15L);
    }

    public boolean isWearingSet(PlayerInventory armor){
        return wearingAllArmor(armor) &&
                armor.getHelmet().getType() == Material.DIAMOND_HELMET &&
                armor.getChestplate().getType() == Material.DIAMOND_CHESTPLATE &&
                armor.getLeggings().getType() == Material.DIAMOND_LEGGINGS &&
                armor.getBoots().getType() == Material.DIAMOND_BOOTS;
    }

    public boolean wearingAllArmor(PlayerInventory armor){
        return (armor.getHelmet() != null &&
                armor.getChestplate() != null &&
                armor.getLeggings() != null &&
                armor.getBoots() != null);
    }
}

package net.frozenorb.foxtrot.gameplay.ability.damage.type;

import net.frozenorb.foxtrot.gameplay.ability.damage.DamageAbility;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Freezer extends DamageAbility {
    @Override
    public String getID() {
        return "freezer";
    }

    @Override
    public String getName() {
        return "&9&lPumpkin's Freezer";
    }

    @Override
    public int getCooldown() {
        return 60 * 4;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.ICE)
                .name(getName())
                .addToLore("&7Hit someone with this to give them", "&7Slowness X and Blindness X")
                .build();
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public void handle(EntityDamageByEntityEvent event, Player damager, Player victim) {
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 6, 10));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 6, 10));

        victim.sendMessage(CC.translate("&fYou were struck by " + getName() + "&f!"));
        damager.sendMessage(CC.translate("&fYou have struck &b" + victim.getName() + " &fwith the " + getName() + "&f."));

        use(damager);
    }
}

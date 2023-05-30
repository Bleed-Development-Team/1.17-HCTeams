package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Powerstone extends InteractAbility {
    @Override
    public String getID() {
        return "powerstone";
    }

    @Override
    public String getName() {
        return "&5&lPowerstone";
    }

    @Override
    public int getCooldown() {
        return 60 * 3;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.PURPLE_DYE)
                .name("&5&lPowerstone")
                .addToLore("&7Right click to receive 5 seconds of Speed II", "&7Strength II and Resistance III")
                .build();
    }

    @Override
    public String getColor() {
        return "&5";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2));

        use(player);

        sendMessage(player, "You have received 5 seconds of Strength II, &fResistance III, and Speed III.");
    }
}

package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Medkit extends InteractAbility {
    @Override
    public String getID() {
        return "medkit";
    }

    @Override
    public String getName() {
        return "&b&lMedkit";
    }

    @Override
    public int getCooldown() {
        return 120;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.PAPER)
                .name("&b&lMedkit")
                .addToLore("&7Right click to receive Resistance III", "&7and Regeneration III")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public String getColor() {
        return "&b";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 7, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 7, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 25, 1));

        use(player);

        sendMessage(player, "You have received 4 seconds of &fResistance III, and Regeneration III.");
    }
}

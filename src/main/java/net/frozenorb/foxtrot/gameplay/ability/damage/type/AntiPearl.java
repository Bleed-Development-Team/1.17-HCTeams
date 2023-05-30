package net.frozenorb.foxtrot.gameplay.ability.damage.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.damage.DamageAbility;
import net.frozenorb.foxtrot.server.event.EnderpearlCooldownAppliedEvent;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class AntiPearl extends DamageAbility {
    @Override
    public String getID() {
        return "antipearl";
    }

    @Override
    public String getName() {
        return "&3&lAnti-Pearl";
    }

    @Override
    public int getCooldown() {
        return 120;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.ENDER_EYE)
                .name(getName())
                .addToLore("&7Puts a player on ender pearl cooldown.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public String getColor() {
        return "&3";
    }

    @Override
    public void handle(EntityDamageByEntityEvent event, Player damager, Player victim) {
        long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(victim.getLocation()) ? 30_000L : HCF.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1_000L : 16_000L;

        EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(victim, timeToApply);
        HCF.getInstance().getServer().getPluginManager().callEvent(appliedEvent);

        EnderpearlCooldownHandler.getEnderpearlCooldown().put(victim.getName(), System.currentTimeMillis() + appliedEvent.getTimeToApply());

        use(damager);
        sendMessage(damager, getColor() + victim.getName() + " &fhas been put on pearl cooldown.");
    }
}

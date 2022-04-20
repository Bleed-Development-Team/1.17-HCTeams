package net.frozenorb.foxtrot.pvpclasses;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class PvPClass implements Listener {

    @Getter String name;
    @Getter String siteLink;
    @Getter int warmup;
    @Getter List<Material> consumables;
    
    private static final Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();

    public PvPClass(String name, int warmup, List<Material> consumables) {
        this.name = name;
        this.siteLink = "www." + Foxtrot.getInstance().getServerHandler().getNetworkWebsite() + "/" + name.toLowerCase().replaceAll(" ", "-");
        this.warmup = warmup;
        this.consumables = consumables;

        // Reduce warmup on kit maps
        if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
            this.warmup = 5;
        }
    }

    public void apply(Player player) {

    }

    public void tick(Player player) {

    }

    public void remove(Player player) {

    }

    public boolean canApply(Player player) {
        return (true);
    }

    public static void removeInfiniteEffects(Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getDuration() > 1_000_000) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public boolean itemConsumed(Player player, Material type) {
        return (true);
    }

    public abstract boolean qualifies(PlayerInventory armor);

    protected boolean wearingAllArmor(PlayerInventory armor) {
        return (armor.getHelmet() != null &&
                armor.getChestplate() != null &&
                armor.getLeggings() != null &&
                armor.getBoots() != null);
    }

    public static void smartAddPotion(final Player player, PotionEffect potionEffect, boolean persistOldValues, PvPClass pvpClass) {
        setRestoreEffect(player, potionEffect);
    }

    @AllArgsConstructor
    public static class SavedPotion {

        @Getter PotionEffect potionEffect;
        @Getter long time;
        @Getter private boolean perm;

    }
    
    public static void setRestoreEffect(Player player, PotionEffect effect) {
        boolean shouldCancel = true;
        Collection<PotionEffect> activeList = player.getActivePotionEffects();
        for (PotionEffect active : activeList) {
            if (!active.getType().equals(effect.getType())) continue;

            // If the current potion effect has a higher amplifier, ignore this one.
            if (effect.getAmplifier() < active.getAmplifier()) {
                return;
            } else if (effect.getAmplifier() == active.getAmplifier()) {
                // If the current potion effect has a longer duration, ignore this one.
                if (0 < active.getDuration() && (effect.getDuration() <= active.getDuration() || effect.getDuration() - active.getDuration() < 10)) {
                    return;
                }
            }

            restores.put(player.getUniqueId(), active.getType(), active);
            shouldCancel = false;
            break;
        }

        // Cancel the previous restore.
        player.addPotionEffect(effect, true);
        if (shouldCancel && effect.getDuration() > 120 && effect.getDuration() < 9600) {
            restores.remove(player.getUniqueId(), effect.getType());
        }
    }

}

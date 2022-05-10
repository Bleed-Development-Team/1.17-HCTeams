package net.frozenorb.foxtrot.extras.enchants.listeners;

import net.frozenorb.foxtrot.extras.enchants.events.PlayerArmorEquipEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class HellForgedEnchant implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().getInventory().getArmorContents() == null) return;
        for (ItemStack item : event.getPlayer().getInventory().getArmorContents()) {
            if (!item.getItemMeta().hasLore()) continue;
            if (!item.getItemMeta().getLore().contains("&4HellForged IV")) continue;

            Damageable damageable = (Damageable) item.getItemMeta();

            if (!damageable.hasDamage()) return;

            damageable.setDamage(damageable.getDamage() + 5);

        }
    }
}

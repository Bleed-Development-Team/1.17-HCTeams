package net.frozenorb.foxtrot.extras.enchants;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.enchants.listeners.FireResistanceEnchant;
import net.frozenorb.foxtrot.extras.enchants.listeners.InvisiblityEnchant;
import net.frozenorb.foxtrot.extras.enchants.listeners.SpeedEnchant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CustomEnchant {
    public static void init() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new FireResistanceEnchant(), Foxtrot.getInstance());
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new SpeedEnchant(), Foxtrot.getInstance());
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new InvisiblityEnchant(), Foxtrot.getInstance());

        Foxtrot.getInstance().getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            Bukkit.getOnlinePlayers().forEach(CustomEnchant::refreshEnchants);
        }, 20L, 20L);
    }

    private static void refreshEnchants(Player player) {
        FireResistanceEnchant.refreshFireResistance(player);
        SpeedEnchant.refreshSpeed(player);
        InvisiblityEnchant.refreshInvisibility(player);
    }
}
package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.extras.ability.item.Items;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NinjaAbility implements Listener {

    Map<UUID, Map<UUID, Long>> hits = new HashMap<>();

    @EventHandler
    public void rightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || player.getItemInHand() == Items.getNinja()){
            if (Cooldown.isOnCooldown("ninja", player)) {
                player.sendMessage(CC.translate("&cYou must wait another &c&l" + Cooldown.getCooldownString(player, "ninja") + "&c seconds to use this again."));
                return;
            }

            if (hits.get(player.getUniqueId()) == null) {
                player.sendMessage(CC.translate("&cNinja star failed to use: No last hit."));
                return;
            }


        }
    }
}

package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.extras.ability.item.Items;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class LauncherAbility implements Listener {

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (player.getItemInHand() == Items.getLauncher()){
            if (Cooldown.isOnCooldown("launcher", player)){
                player.sendMessage(CC.translate("&cYou cannot use this for another &c&l" + Cooldown.getCooldownString(player, "bone") + "&c!"));
                event.setCancelled(true);

                return;
            }

            player.setVelocity(player.getLocation().getDirection().multiply(2));
        }
    }
}

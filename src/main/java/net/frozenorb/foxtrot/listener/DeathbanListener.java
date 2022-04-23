package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.LastInvCommand;
import net.frozenorb.foxtrot.server.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathbanListener implements Listener {

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        LastInvCommand.recordInventory(event.getEntity());

        EnderpearlCooldownHandler.getEnderpearlCooldown().remove(event.getEntity().getName()); // cancel enderpearls

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        if (Foxtrot.getInstance().getInDuelPredicate().test(event.getEntity())) {
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
        		return;
        }

        int seconds = (int) Foxtrot.getInstance().getServerHandler().getDeathban(event.getEntity());

        final String time = TimeUtils.formatIntoDetailedString(seconds);

        new BukkitRunnable() {

            public void run() {
                if (!event.getEntity().isOnline()) {
                    return;
                }

                if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
                    event.getEntity().kickPlayer(ChatColor.YELLOW + "Come back tomorrow for SOTW!");
                } else {
                    if (Foxtrot.getInstance().getFriendLivesMap().getLives(event.getEntity().getPlayer().getUniqueId()) > 0) {
                        Foxtrot.getInstance().getFriendLivesMap().setLives(event.getEntity().getPlayer().getUniqueId(), Foxtrot.getInstance().getFriendLivesMap().getLives(event.getEntity().getPlayer().getUniqueId()) - 1);
                    } else {
                        //Foxtrot.getInstance().getDeathbanMap().deathban(event.getEntity().getUniqueId(), seconds);

                        //event.getEntity().getPlayer().kickPlayer(ChatColor.YELLOW + "Come back in " + time + "!");
                    }
                }
            }

        }.runTaskLater(Foxtrot.getInstance(), 10 * 20L);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean shouldBypass = event.getPlayer().isOp();
        
        if (!shouldBypass) {
            shouldBypass = event.getPlayer().hasPermission("hcteams.staff");
        }
        
        if (shouldBypass) {
            Foxtrot.getInstance().getDeathbanMap().revive(event.getPlayer().getUniqueId());
        }
    }

}
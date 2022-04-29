package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.ApplyDeathbanKit;
import net.frozenorb.foxtrot.commands.LastInvCommand;
import net.frozenorb.foxtrot.server.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.DeathbanUtils;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
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
                    event.getEntity().kickPlayer(CC.translate("&cYou've died on EOTW! Keep an eye out for the next SOTW!"));
                } else {
                    if (Foxtrot.getInstance().getFriendLivesMap().getLives(event.getEntity().getPlayer().getUniqueId()) > 0) {
                        Foxtrot.getInstance().getFriendLivesMap().setLives(event.getEntity().getPlayer().getUniqueId(), Foxtrot.getInstance().getFriendLivesMap().getLives(event.getEntity().getPlayer().getUniqueId()) - 1);
                    } else {
                        event.getEntity().getPlayer().spigot().respawn();
                        DeathbanUtils.teleportToDeathban(event.getEntity().getPlayer());

                        Foxtrot.getInstance().getDeathbanMap().deathban(event.getEntity().getPlayer().getUniqueId(), seconds);
                    }
                }
            }

        }.runTaskLater(Foxtrot.getInstance(), 20L);
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

    /*
        DEATHBAN ARENA LISTENERS
     */

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            DeathbanUtils.teleportToDeathban(player);
        }
    }



    @EventHandler
    public void leave(PlayerQuitEvent event){
        if(ApplyDeathbanKit.people.contains(event.getPlayer().getUniqueId())){
            event.getPlayer().getInventory().clear();
            for (PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
                event.getPlayer().removePotionEffect(effect.getType());
            }
        }
        ApplyDeathbanKit.people.remove(event.getPlayer().getUniqueId());
    }
}
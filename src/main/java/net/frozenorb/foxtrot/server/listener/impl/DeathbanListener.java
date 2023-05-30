package net.frozenorb.foxtrot.server.listener.impl;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.commands.staff.LastInvCommand;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.entity.Player;
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

        if (HCF.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        if (HCF.getInstance().getInDuelPredicate().test(event.getEntity())) {
            return;
        }

        if (HCF.getInstance().getServerHandler().isVeltKitMap()) {
        		return;
        }

        int seconds = (int) HCF.getInstance().getServerHandler().getDeathban(event.getEntity());

        final String time = TimeUtils.formatIntoDetailedString(seconds);

        new BukkitRunnable() {

            public void run() {
                if (!event.getEntity().isOnline()) {
                    return;
                }

                if (HCF.getInstance().getServerHandler().isPreEOTW()) {
                    event.getEntity().kickPlayer(CC.translate("&cYou've died on EOTW! Keep an eye out for the next SOTW!"));
                } else {
                    event.getEntity().spigot().respawn();

                    event.getEntity().teleport(HCF.getInstance().getServerHandler().getSpawnLocation());
                }
            }

        }.runTaskLater(HCF.getInstance(), 5);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean shouldBypass = event.getPlayer().isOp();
        
        if (!shouldBypass) {
            shouldBypass = event.getPlayer().hasPermission("hcteams.staff");
        }
        
        if (shouldBypass) {
            HCF.getInstance().getDeathbanMap().revive(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (HCF.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            int seconds = (int) HCF.getInstance().getServerHandler().getDeathban(event.getPlayer());

            event.getPlayer().kickPlayer(CC.translate("&cYou are currently deathbanned!\n\nYou're deathbanned for another " +
                    TimeUtils.formatIntoDetailedString(seconds) + "."));
        }
    }
}
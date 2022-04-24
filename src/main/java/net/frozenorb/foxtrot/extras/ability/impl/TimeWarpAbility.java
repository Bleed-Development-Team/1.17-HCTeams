package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeWarpAbility extends Ability implements Listener {

    Map<UUID, Location> timewarp = new HashMap<>();

    @Override
    public String getName() {
        return "&e&lTime-Warp";
    }

    @Override
    public String getUncoloredName() {
        return "Time-Warp";
    }

    @Override
    public String getDescription() {
        return "Right click to warp to your last pearl.";
    }

    @Override
    public String getCooldownID() {
        return "warp";
    }

    @Override
    public int getCooldown() {
        return 60 * 2;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getTimeWarp();
    }

    @EventHandler
    public void click(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (!isSimilarTo(player.getItemInHand(), Items.getTimeWarp())) return;
            if (!canUse(player)) return;

            if (!timewarp.containsKey(player.getUniqueId())){
                player.sendMessage(CC.translate("&c❤ &6No last pearl found!"));
                return;
            }
            player.sendMessage(CC.translate("&c❤ &6Teleporting to last pearl in &f3 &6seconds!"));

            player.sendMessage(CC.translate("&c❤ &6Teleporting to last pearl in &f3 &6seconds!"));

            new BukkitRunnable(){
                int i = 0;
                @Override
                public void run() {
                    if (i == 1){
                        player.sendMessage(CC.translate("&c❤ &6Teleporting to last pearl in &f2 &6seconds!"));
                    } else if (i == 2){
                        player.sendMessage(CC.translate("&c❤ &6Teleporting to last pearl in &f1 &6second!"));
                    } else if (i == 3){
                        player.teleport(timewarp.get(player.getUniqueId()));
                        player.sendMessage(CC.translate("&c❤ &6You have been teleported to your thrown last pearl."));

                        timewarp.remove(player.getUniqueId());
                        cancel();
                    }
                    i++;
                }
            }.runTaskTimer(Foxtrot.getInstance(), 20L, 20L);

        }
    }

    @EventHandler
    public void launchProjectile(ProjectileLaunchEvent event){
        if (!(event.getEntity().getShooter() instanceof Player player)) return;

        if (event.getEntity() instanceof EnderPearl){
            timewarp.put(player.getUniqueId(), player.getLocation());

            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
               timewarp.remove(player.getUniqueId());
            }, 20L * 13L);
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event){
        if (timewarp.get(event.getPlayer().getUniqueId()) != null) timewarp.remove(event.getPlayer().getUniqueId());
    }
}

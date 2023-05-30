package net.frozenorb.foxtrot.server.listener.impl;

import com.google.common.collect.ImmutableSet;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.util.MaterialUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static org.bukkit.block.BlockFace.*;

public class AntiGlitchListener implements Listener {
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onVerticalBlockPlaceGlitch(BlockPlaceEvent event) {
        if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null && event.isCancelled() && !event.getPlayer().hasMetadata("ImmuneFromGlitchCheck")) {
            event.getPlayer().teleport(event.getPlayer().getLocation());
            event.getPlayer().setNoDamageTicks(0);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBoatMove(VehicleMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())
            return;
        
        Block block = to.getBlock();
        if (block.getType() == Material.OAK_FENCE_GATE || block.getType() == Material.SPRUCE_FENCE_GATE || block.getType() == Material.BIRCH_FENCE_GATE || block.getType() == Material.JUNGLE_FENCE_GATE || block.getType() == Material.DARK_OAK_FENCE_GATE || block.getType() == Material.ACACIA_FENCE_GATE) {
            event.getVehicle().teleport(from);
            List<Entity> passenger = event.getVehicle().getPassengers();
            if (passenger instanceof Player) {
                ((Player) passenger).sendMessage(ChatColor.RED + "You can't move your boat into a fence gate.");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerBoatMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        assert to != null;
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())
            return;
        
        Player player = event.getPlayer();
        if (player.getVehicle() != null && player.getVehicle() instanceof Boat) {
            if (to.getBlock().getType() == Material.OAK_FENCE_GATE || to.getBlock().getType() == Material.SPRUCE_FENCE_GATE || to.getBlock().getType() == Material.BIRCH_FENCE_GATE || to.getBlock().getType() == Material.JUNGLE_FENCE_GATE || to.getBlock().getType() == Material.DARK_OAK_FENCE_GATE || to.getBlock().getType() == Material.ACACIA_FENCE_GATE) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can't move your boat into a fence gate.");
            }
        }
    }
    
    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.getBlock().getType().name().contains("RAIL")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType().name().contains("RAIL")) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) {
            return;
        }
        
        final Player player = (Player) event.getExited();
        Location location = player.getLocation();
        
        while (location.getBlock().getType().isSolid()) {
            location.add(0, 1, 0);
            if (location.getBlockY() == 255) {
                break;
            }
        }
        
        while (location.getBlock().getType().isSolid()) {
            location.subtract(0, 1, 0);
            if (location.getBlockY() == 1) {
                break;
            }
        }
        
        final Location locationFinal = location;
        
        new BukkitRunnable() {
            
            public void run() {
                player.teleport(locationFinal);
            }
            
        }.runTaskLater(HCF.getInstance(), 1L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent event) {
        
        if (event.getVehicle() instanceof Horse || event.getVehicle() instanceof Minecart) {
            return;
        }
        
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getWorld().getEnvironment() != World.Environment.NETHER) {
            return;
        }
        
        if (event.getEntity() instanceof Skeleton) {

            event.getDrops().removeIf(item -> item.getType() == Material.PLAYER_HEAD);
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (player.getGameMode() == GameMode.CREATIVE || player.getWorld().getEnvironment() != World.Environment.NETHER) {
            return;
        }
        
        if (event.getBlock().getType() == Material.SPAWNER) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You aren't allowed to place mob spawners in the nether.");
        }
    }
    
    private static final ImmutableSet<BlockFace> SURROUNDING = ImmutableSet.of(SELF, NORTH, NORTH_EAST, NORTH_WEST, SOUTH, SOUTH_EAST, SOUTH_WEST, EAST, WEST, UP);
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void denyDismountClipping(VehicleExitEvent event) {
        // Do nothing if exited was not a player.
        if (!(event.getExited() instanceof Player))
            return;
        
        // Do nothing if player has permission.
        Player player = (Player) event.getExited();
        
        // Locate a safe position to teleport the player.
        Location pLoc = player.getLocation();
        Location vLoc = event.getVehicle().getLocation();
        if (player.getLocation().getY() > 250.0D) {
            pLoc.add(0, 10, 0);
        } else if (!MaterialUtils.isFullBlock(vLoc.add(0.0D, 1.0D, 0.0D).getBlock().getType())) {
            // If the vehicles' position is safe, teleport the player into the center of the
            // block, otherwise below.
            if (!MaterialUtils.isFullBlock(vLoc.getBlock().getType())) {
                pLoc = new Location(vLoc.getWorld(), vLoc.getBlockX() + 0.5, vLoc.getBlockY(), vLoc.getBlockZ() + 0.5, pLoc.getYaw(), pLoc.getPitch());
            } else {
                pLoc.subtract(0, 1, 0);
            }
        }
        
        final Location finalLocation = pLoc;
        // Teleport player to the safe location on the next tick.
        Bukkit.getScheduler().runTask(HCF.getInstance(), () -> player.teleport(finalLocation));
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void denyDismountClipping(CreatureSpawnEvent event) {
        // Do nothing if entity is not a horse.
        if (event.getEntityType() != EntityType.HORSE)
            return;
        
        if (HCF.getInstance().getServerHandler().isHardcore()) {
            return;
        }

        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void denyDismountGlitching(PlayerDeathEvent event) {
        if (event.getEntity().isInsideVehicle())
            event.getEntity().getVehicle().remove();
    }
}

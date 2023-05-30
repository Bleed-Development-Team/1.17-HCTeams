package net.frozenorb.foxtrot.server.listener.impl;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.commands.op.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class  SpawnListener implements Listener {

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            if (HCF.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
                return;
            }
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        } else if (DTRBitmask.ROAD.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }

    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (HCF.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in spawn!");
        } else if (DTRBitmask.ROAD.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build on the road!");
        } else if (HCF.getInstance().getServerHandler().isSpawnBufferZone(event.getBlock().getLocation()) || HCF.getInstance().getServerHandler().isNetherBufferZone(event.getBlock().getLocation()) && HCF.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
            //if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation()) && event.getItemInHand() != null && event.getItemInHand().getType() == Material.COBWEB && (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap())) {
                for (Event playableEvent : HCF.getInstance().getEventHandler().getEvents()) {
                    if (!playableEvent.isActive() || !(playableEvent instanceof KOTH)) {
                        continue;
                    }
                    
                    KOTH koth = (KOTH) playableEvent;
                    
                    if (koth.onCap(event.getBlockPlaced().getLocation())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "You can't place web on cap!");
                        event.getPlayer().setItemInHand(null);
                        
                        event.getPlayer().setMetadata("ImmuneFromGlitchCheck", new FixedMetadataValue(HCF.getInstance(), new Object()));
                        
                        Bukkit.getScheduler().runTask(HCF.getInstance(), () -> {
                            event.getPlayer().removeMetadata("ImmuneFromGlitchCheck", HCF.getInstance());
                        });
                        
                        return;
                    }
                }
                
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (event.getBlock().getType() == Material.COBWEB) {
                            event.getBlock().setType(Material.AIR);
                        }
                    }

                }.runTaskLater(HCF.getInstance(), 10 * 20L);
            //} else {
                //event.setCancelled(true);
                //event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build this close to spawn!");
            //}
        }
        if (HCF.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
            if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

            event.setCancelled(true);
            event.getPlayer().sendMessage(CC.translate("&eYou cannot build in &cWarzone&e's territory!"));
        }


    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (HCF.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in spawn!");
        } else if (DTRBitmask.ROAD.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build on the road!");
        }else if (HCF.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
            if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE || HCF.getInstance().getServerHandler().isEOTW()) return;

            event.setCancelled(true);
            event.getPlayer().sendMessage(CC.translate("&eYou cannot build in &cWarzone&e's territory!"));
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (HCF.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player) || HCF.getInstance().getServerHandler().isAdminOverride((Player) event.getRemover())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        } else if (DTRBitmask.ROAD.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME || HCF.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getRightClicked().getLocation())) {
            event.setCancelled(true);
        } else if (DTRBitmask.ROAD.appliesAt(event.getRightClicked().getLocation())) {
            event.setCancelled(true);
        }
    }

    // Used for item frames
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getEntity().getType() != EntityType.ITEM_FRAME || HCF.getInstance().getServerHandler().isAdminOverride((Player) event.getDamager())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (HCF.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        /*
        if (Foxtrot.getInstance().getServerHandler().isSpawnBufferZone(event.getBlockClicked().getLocation())) {

            final Block waterBlock = event.getBlockClicked().getRelative(event.getBlockFace());


            if (waterBlock.getRelative(BlockFace.NORTH).isLiquid() || waterBlock.getRelative(BlockFace.SOUTH).isLiquid() || waterBlock.getRelative(BlockFace.EAST).isLiquid() || waterBlock.getRelative(BlockFace.WEST).isLiquid()) {
                //event.setCancelled(true);
                return;
            }

            RegenUtils.schedule(waterBlock, 30, TimeUnit.SECONDS, (block) -> InventoryUtils.fillBucket(event.getPlayer()), (block) -> true);

        }

         */
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if ((event.getEntity() instanceof Player || event.getEntity() instanceof Horse) && DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamageByEntity2(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = TeamListener.getDamageSource(event.getDamager()); // find the player damager if one exists

        if (damager != null) {
            Player victim = (Player) event.getEntity();

            if (DTRBitmask.SAFE_ZONE.appliesAt(victim.getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(damager.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Team team = LandBoard.getInstance().getTeam(event.getPlayer().getLocation());
        if (team != null && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE) && (HCF.getInstance().getServerHandler().isVeltKitMap() || CustomTimerCreateCommand.isSOTWTimer())) {
            event.getItemDrop().remove();
        }
    }
}
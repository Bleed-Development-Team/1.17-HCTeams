package net.frozenorb.foxtrot.listener;

import com.google.common.collect.Maps;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.map.kits.Kit;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class KitMapListener implements Listener {
    
    private static Map<UUID, String> kitEditing = Maps.newHashMap();
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (Foxtrot.getInstance().getMapHandler().getScoreboardTitle().contains("cane")) {
            return;
        }
        
        Player victim = e.getEntity();

        // 1. killer should not be null
        // 2. victim should not be equal to killer
        // 3. victim should not be naked
        if (victim.getKiller() != null && !victim.getUniqueId().equals(victim.getKiller().getUniqueId()) && !Players.isNaked(victim)) {
            String killerName = victim.getKiller().getName();
            FrozenEconomyHandler.deposit(victim.getKiller().getUniqueId(), 100 + getAdditional(victim.getKiller()));
            victim.getKiller().sendMessage(ChatColor.RED + "You received a reward for killing " + ChatColor.GREEN
                    + victim.getName() + ChatColor.RED + ".");
            
            Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
                int kills = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(victim.getKiller()).getKills();
                if (kills % 10 == 0) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + killerName + " OP 1");
                    victim.getKiller().sendMessage(ChatColor.GREEN + "You received an OP key for 10 kills!");
                }
            });
        }
    }
    
    private int getAdditional(Player killer) {
        if (killer.hasPermission("hcteams.killreward.ghoul")) {
            return 5;
        } else if (killer.hasPermission("hcteams.killreward.poltergeist")) {
            return 5;
        } else if (killer.hasPermission("hcteams.killreward.sorcerer")) {
            return 10;
        } else if (killer.hasPermission("hcteams.killreward.suprive")) {
            return 25;
        } else if (killer.hasPermission("hcteams.killreward.juggernaut")) {
            return 50;
        } else if (killer.hasPermission("hcteams.killreward.myth")) {
            return 75;
        } else if (killer.hasPermission("hcteams.killreward.sapphire")) {
            return 100;
        } else if (killer.hasPermission("hcteams.killreward.pearl")) {
            return 125;
        } else if (killer.hasPermission("hcteams.killreward.ruby")) {
            return 150;
        } else if (killer.hasPermission("hcteams.killreward.velt")) {
            return 175;
        } else if (killer.hasPermission("hcteams.killreward.velt-plus")) {
            return 200;
        } else {
            return 0;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), event.getEntity()::remove, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());
        if (team != null && event.getEntity() instanceof Arrow && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        if (command.startsWith("/pv") || command.startsWith("/playervault") || command.startsWith("pv") || command.startsWith("playervaults") || command.startsWith("/vault") || command.startsWith("vault") || command.startsWith("vc") || command.startsWith("/vc")) {
            if (SpawnTagHandler.isTagged(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You can't /pv in combat.");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (kitEditing.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You're unable to drop items whilst in the kit editor...");
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (kitEditing.containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (kitEditing.containsKey(event.getPlayer().getUniqueId())) {
            kitEditing.remove(event.getPlayer().getUniqueId());
        }
        
        Foxtrot.getInstance().getMapHandler().getKitManager().logout(event.getPlayer().getUniqueId());
    }

    /*
    @Command(names = {"kits edit", "kit edit"}, permission = "")
    public static void editKit(Player player, @Param(name = "kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Unable to locate kit...");
            return;
        }
        
        if (!kit.getName().equalsIgnoreCase("pvp") && !kit.getName().equalsIgnoreCase("archer") && !kit.getName().equalsIgnoreCase("bard")) {
            player.sendMessage(ChatColor.RED + "Unable to edit this kit...");
            return;
        }
        
        if (kitEditing.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You already seem to be editing a kit...");
            return;
        }
        
        Team team = LandBoard.getInstance().getTeam(player.getLocation());
        if (team == null || !team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
            player.sendMessage(ChatColor.RED + "You can only edit a kit from inside spawn!");
            return;
        }
        
        kitEditing.put(player.getUniqueId(), kit.getName());
        
        Inventory inventory = Bukkit.createInventory(player, kit.getInventoryContents().length, "Kit editor");
        inventory.setContents(Arrays.copyOf(kit.getInventoryContents(), kit.getInventoryContents().length));
        player.openInventory(inventory);
    }
     */
    
    @EventHandler(ignoreCancelled = true)
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        
        if (event.getView().getTitle().equals("Kit editor")) {
            String editingKit = kitEditing.remove(player.getUniqueId());
            
            if (editingKit == null) {
                Bukkit.getLogger().info("wtf");
                return;
            }
            
            Foxtrot.getInstance().getMapHandler().getKitManager().saveKit(event.getPlayer().getUniqueId(), editingKit, inventory.getContents());
            player.sendMessage(ChatColor.GREEN + "Successfully saved kit!");
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Foxtrot.getInstance().getMapHandler().getKitManager().loadKits(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        if (event.getCause() != TeleportCause.NETHER_PORTAL) {
            return;
        }
        
        if (event.getTo().getWorld().getEnvironment() != Environment.NETHER) {
            return;
        }
        
        event.setTo(event.getTo().getWorld().getSpawnLocation().clone());
    }

    /*
    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        event.getInventory();
        if (event.getClickedInventory() == null) {
            return;
        }
        
        if (!event.getView().getTitle().equals("Kit editor") && !event.getView().getTitle().equals("Kit editor")) {
            return;
        }
        
        // now we know that they're in the kit editor
        if (!event.getClickedInventory().equals("Kit editor")) {
            event.setCancelled(true); // they can only click shit in the kit editor
            return;
        }
        
        if (event.getClickedInventory() != event.getInventory()) {
            event.setCancelled(true);
            return;
        }
        
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
            return;
        }
        
        if (!event.getClickedInventory().getName().equals("Kit editor")) {
            event.setCancelled(true);
            return;
        }
        
        if (event.getClickedInventory().getName().equals("Kit editor") && event.getAction().name().startsWith("DROP")) {
            event.setCancelled(true);
            return;
        }
        
        if (event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            event.setCancelled(true);
            return;
        }
        
        if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
            event.setCancelled(true);
            return;
        }
        
        Bukkit.getLogger().info(event.getViewers().get(0).getName() + ": " + event.getAction().toString());
    }
     */
    
}

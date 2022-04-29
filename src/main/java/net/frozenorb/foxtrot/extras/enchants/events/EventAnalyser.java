package net.frozenorb.foxtrot.extras.enchants.events;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class EventAnalyser implements Listener {

    private final Plugin plugin;
    private final ConcurrentMap<UUID, ItemStack[]> contents = new ConcurrentHashMap<>();

    public EventAnalyser(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getOnlinePlayers().forEach(player -> getContents().putIfAbsent(player.getUniqueId(), player.getEquipment().getArmorContents()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public final void onEvent(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        final Inventory inventory = event.getClickedInventory();
        if (inventory != null && (inventory.getType() == InventoryType.CRAFTING || inventory.getType() == InventoryType.PLAYER)) {
            if (event.getSlotType() == InventoryType.SlotType.ARMOR || event.isShiftClick()) {
                check((Player) event.getWhoClicked());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public final void onEvent(final PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            final ItemStack item = event.getItem();
            if (item == null) {
                return;
            }
            final String name = item.getType().name();

            if (name.contains("_HELMET") || name.contains("_CHESTPLATE") || name.contains("_LEGGINGS") || name.contains("_BOOTS")) {
                check(event.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public final void onEvent(final PlayerDeathEvent event) {
        check(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public final void onEvent(final PlayerJoinEvent event) {
        check(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public final void onEvent(final PlayerQuitEvent event) {
        if (getContents().containsKey(event.getPlayer().getUniqueId())) {
            getContents().remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public final void onEvent(final BlockDispenseEvent event) {
        final ItemStack item = event.getItem();
        final Location location = event.getBlock().getLocation();
        if (item != null) {
            location.getWorld().getNearbyEntities(location, 6, 6, 6).stream().filter(e -> e instanceof Player).map(e -> (Player) e).forEach(player -> check(player));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public final void onEvent(final PlayerItemBreakEvent event) {
        check(event.getPlayer());
    }

    private void check(final Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack[] now = player.getEquipment().getArmorContents();
                ItemStack[] saved = getContents().get(player.getUniqueId());
                for (int i = 0; i < now.length; i++) {
                    if (now[i] == null && (saved != null && saved[i] != null)) {
                        Bukkit.getPluginManager().callEvent(new PlayerArmorUnequipEvent(player, saved[i]));
                    } else if (now[i] != null && (saved == null || saved[i] == null)) {
                        Bukkit.getPluginManager().callEvent(new PlayerArmorEquipEvent(player, now[i]));
                    } else if (saved != null && (now[i] != null && saved[i] != null && !now[i].toString().equalsIgnoreCase(saved[i].toString()))) {
                        Bukkit.getPluginManager().callEvent(new PlayerArmorUnequipEvent(player, saved[i]));
                        Bukkit.getPluginManager().callEvent(new PlayerArmorEquipEvent(player, now[i]));
                    }
                }
                getContents().put(player.getUniqueId(), now);
            }
        }.runTaskLater(plugin, 1L);
    }

    private ConcurrentMap<UUID, ItemStack[]> getContents() {
        return contents;
    }
}

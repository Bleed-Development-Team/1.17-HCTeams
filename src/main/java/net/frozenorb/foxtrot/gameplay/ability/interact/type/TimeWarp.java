package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeWarp extends InteractAbility {
    public static Map<UUID, Location> enderPearl = new HashMap<>();

    @Override
    public String getID() {
        return "timewarp";
    }

    @Override
    public String getName() {
        return "&e&lTimewarp";
    }

    @Override
    public int getCooldown() {
        return 60;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.FEATHER)
                .name(getName())
                .addToLore("&7Go back to your last thrown pearl in 15 seconds.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public String getColor() {
        return "&e";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        if (!enderPearl.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have not thrown a pearl in the last 16 seconds...");
            return;
        }

        if (player.hasMetadata("ninjastar")) {
            player.sendMessage(ChatColor.RED + "You may not use a " + getName() + ChatColor.RED + " whilst someone is using a Ninja Star on you!");
            return;
        }

        use(player);

        final Location location = enderPearl.remove(player.getUniqueId()).clone();

        new BukkitRunnable() {
            private int seconds = 4;

            @Override
            public void run() {
                this.seconds--;

                if (!event.getPlayer().isOnline()) {
                    this.cancel();
                    return;
                }

                if (this.seconds <= 0) {
                    player.teleport(location);

                    this.cancel();
                    return;
                }

                event.getPlayer().sendMessage(ChatColor.GREEN + "Teleporting in " + ChatColor.WHITE + this.seconds + ChatColor.GREEN + " second" + (this.seconds == 1 ? "":"s") + "...");
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        }.runTaskTimer(HCF.getInstance(),0L,20L);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    private void onLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof EnderPearl) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player shooter = (Player) event.getEntity().getShooter();

        final Location location = shooter.getLocation();

        enderPearl.remove(shooter.getUniqueId());
        enderPearl.put(shooter.getUniqueId(), shooter.getLocation());

        HCF.getInstance().getServer().getScheduler().runTaskLater(HCF.getInstance(), () -> {
            if (!enderPearl.containsKey(shooter.getUniqueId())) {
                return;
            }

            final Location newLocation = enderPearl.get(shooter.getUniqueId());

            if (location.getX() != newLocation.getX() || location.getY() != newLocation.getY() || location.getZ() != newLocation.getZ()) {
                return;
            }

            enderPearl.remove(shooter.getUniqueId());
        }, 20*16);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPearl(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getItem() == null || !event.getAction().name().contains("RIGHT")) {
            return;
        }

        if (event.getItem().getType() != Material.ENDER_PEARL) {
            return;
        }

        if (EnderpearlCooldownHandler.getEnderpearlCooldown().containsKey(player.getName())) {
            return;
        }

        final Location location = player.getLocation().clone();

        enderPearl.remove(player.getUniqueId());
        enderPearl.put(player.getUniqueId(), location);

        HCF.getInstance().getServer().getScheduler().runTaskLater(HCF.getInstance(), () -> {
            if (!enderPearl.containsKey(player.getUniqueId())) {
                return;
            }

            final Location newLocation = enderPearl.get(player.getUniqueId());

            if (location.getX() != newLocation.getX() || location.getY() != newLocation.getY() || location.getZ() != newLocation.getZ()) {
                return;
            }

            enderPearl.remove(player.getUniqueId());
        }, 20*15);
    }
}

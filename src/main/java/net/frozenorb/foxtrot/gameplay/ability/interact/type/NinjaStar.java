package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.server.event.EnderpearlCooldownAppliedEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NinjaStar extends InteractAbility {

    Map<UUID, LastDamageEntry> cache = new HashMap<>();

    @Override
    public String getID() {
        return "ninjastar";
    }

    @Override
    public String getName() {
        return "&9&lNinja Star";
    }

    @Override
    public int getCooldown() {
        return 60 * 4;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.NETHER_STAR)
                .name(getName())
                .addToLore("&7Right click to teleport whoever hit you in the last 30 seconds.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public String getColor() {
        return "&9";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        final long difference = TimeUnit.SECONDS.toMillis(30L);

        if (!cache.containsKey(player.getUniqueId()) || (System.currentTimeMillis() - cache.get(player.getUniqueId()).getTime()) > difference) {
            player.sendMessage(ChatColor.RED + "No player has hit you within the last 30 seconds.");
            return;
        }

        final LastDamageEntry entry = cache.get(player.getUniqueId());
        final Player target = HCF.getInstance().getServer().getPlayer(entry.getUuid());

        if (target.isOnline()) {

            target.setMetadata("ninja", new FixedMetadataValue(HCF.getInstance(), true));
            target.sendMessage(ChatColor.RED + "You may not use an enderpearl or timewarp...");

            long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(target.getLocation()) ? 30_000L : HCF.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1_000L : 16_000L;

            EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(target, timeToApply);
            HCF.getInstance().getServer().getPluginManager().callEvent(appliedEvent);

            HCF.getInstance().getServer().getScheduler().runTaskLater(HCF.getInstance(), () -> {
                target.removeMetadata("ninja", HCF.getInstance());
            }, 20*5);
        }

        player.sendMessage(CC.translate("&cTeleporting to &f" + target.getName() + " &cin 3 seconds..."));

        new BukkitRunnable() {
            private int seconds = 3;

            @Override
            public void run() {

                if (!event.getPlayer().isOnline()) {
                    this.cancel();
                    return;
                }

                final Player target = HCF.getInstance().getServer().getPlayer(entry.getUuid());

                if (this.seconds < 1) {

                    final Location location = (target != null && target.isOnline()) ? target.getLocation():entry.getLocation();

                    event.getPlayer().teleport(location);

                    this.cancel();
                    return;
                }

                this.seconds--;

                event.getPlayer().sendMessage(ChatColor.YELLOW + "Teleporting to " + ChatColor.WHITE + target.getName() + ChatColor.YELLOW + " in " + ChatColor.RED + (this.seconds+1) + ChatColor.YELLOW + " second" + (this.seconds == 1 ? "":"s") + "...");

                if (target != null && target.isOnline()) {
                    target.sendMessage(player.getName() + ChatColor.RED + " will teleport to you in " + ChatColor.WHITE + (this.seconds+1) + ChatColor.RED + " second" + (this.seconds == 1 ? "":"s") + ".");
                }
            }

        }.runTaskTimer(HCF.getInstance(),0L,20L);

        use(player, "");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        cache.put(event.getEntity().getUniqueId(),new LastDamageEntry(System.currentTimeMillis(),event.getDamager().getUniqueId(),event.getDamager().getLocation()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {

        if (event.isCancelled() || event.getDamager() instanceof EnderPearl) {
            return;
        }

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Projectile)) {
            return;
        }

        if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) {
            return;
        }

        final Player damager = (Player) ((Projectile) event.getDamager()).getShooter();

        cache.put(event.getEntity().getUniqueId(),new LastDamageEntry(System.currentTimeMillis(),damager.getUniqueId(),damager.getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(PlayerDeathEvent event) {
        cache.remove(event.getEntity().getUniqueId());

        final Optional<Map.Entry<UUID, LastDamageEntry>> optionalLastDamageEntry = cache.entrySet().stream().filter(it -> it.getValue().getUuid().toString().equalsIgnoreCase(event.getEntity().getUniqueId().toString())).findFirst();

        if (!optionalLastDamageEntry.isPresent()) {
            return;
        }

        cache.remove(optionalLastDamageEntry.get().getKey(), optionalLastDamageEntry.get().getValue());
    }

    @AllArgsConstructor
    public static class LastDamageEntry {

        @Getter
        private long time;
        @Getter
        private UUID uuid;
        @Getter
        private Location location;

    }
}

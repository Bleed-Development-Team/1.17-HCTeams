package net.frozenorb.foxtrot.server.pearl;

import lombok.Getter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.server.event.EnderpearlCooldownAppliedEvent;
import net.frozenorb.foxtrot.server.event.PlayerPearlRefundEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnderpearlCooldownHandler implements Listener {

	@Getter private static Map<String, Long> enderpearlCooldown = new ConcurrentHashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player shooter = (Player) event.getEntity().getShooter();

		if (event.getEntity() instanceof EnderPearl) {
			// Store the player's enderpearl in-case we need to remove it prematurely
			shooter.setMetadata("LastEnderPearl", new FixedMetadataValue(HCF.getInstance(), event.getEntity()));

			// Get the default time to apply (in MS)
			long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(event.getEntity().getLocation()) ? 30_000L : HCF.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1_000L : 16_000L;

			// Call our custom event (time to apply needs to be modifiable)
			EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(shooter, timeToApply);
			HCF.getInstance().getServer().getPluginManager().callEvent(appliedEvent);

			// Put the player into the cooldown map
			enderpearlCooldown.put(shooter.getName(), System.currentTimeMillis() + appliedEvent.getTimeToApply());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof EnderPearl)) {
			return;
		}

		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player thrower = (Player) event.getEntity().getShooter();

		if (enderpearlCooldown.containsKey(thrower.getName()) && enderpearlCooldown.get(thrower.getName()) > System.currentTimeMillis()) {
			long millisLeft = enderpearlCooldown.get(thrower.getName()) - System.currentTimeMillis();

			double value = (millisLeft / 1000D);
			double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1; // don't tell user 0.0

			event.setCancelled(true);
			thrower.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
			thrower.updateInventory();
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerTeleport(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof EnderPearl)) return;

		EnderPearl enderPearl = (EnderPearl) event.getEntity();
		Player player = (Player) enderPearl.getShooter();

		if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
			event.setCancelled(true);
			player.sendMessage(CC.translate("&cYou are not allowed to use an enderpearl in this region."));

			Bukkit.getPluginManager().callEvent(new PlayerPearlRefundEvent(player));

			return;
		}

		if (DTRBitmask.NO_ENDERPEARL.appliesAt(player.getLocation())) {
			event.setCancelled(true);
			player.sendMessage(CC.translate("&cYou are not allowed to use an enderpearl in this region."));

			Bukkit.getPluginManager().callEvent(new PlayerPearlRefundEvent(player));

			return;
		}

        /*
        Material mat = event.getTo().getBlock().getType();

        if (((mat == Material.THIN_GLASS || mat == Material.IRON_FENCE) && clippingThrough(target, from, 0.65)) || ((mat == Material.FENCE || mat == Material.NETHER_FENCE || mat == Material.FENCE_GATE) && clippingThrough(target, from, 0.45))) {
            event.setTo(from);
            return;
        }

        target.setX(target.getBlockX() + 0.5);
        target.setZ(target.getBlockZ() + 0.5);
        event.setTo(target);
        */
	}

	@EventHandler
	public void onRefund(PlayerPearlRefundEvent event) {
		Player player = event.getPlayer();

		if (!player.isOnline()) {
			return;
		}

		ItemStack inPlayerHand = player.getItemInHand();
		if (inPlayerHand != null && inPlayerHand.getType() == Material.ENDER_PEARL && inPlayerHand.getAmount() < 16) {
			inPlayerHand.setAmount(inPlayerHand.getAmount() + 1);
			player.updateInventory();
		}

		enderpearlCooldown.remove(player.getName());
	}


	public boolean clippingThrough(Location target, Location from, double thickness) {
		return ((from.getX() > target.getX() && (from.getX() - target.getX() < thickness)) || (target.getX() > from.getX() && (target.getX() - from.getX() < thickness)) ||
		        (from.getZ() > target.getZ() && (from.getZ() - target.getZ() < thickness)) || (target.getZ() > from.getZ() && (target.getZ() - from.getZ() < thickness)));
	}

	public static void resetEnderpearlTimer(Player player) {
		if (DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(player.getLocation())) {
			enderpearlCooldown.put(player.getName(), System.currentTimeMillis() + 30_000L);
		} else {
			enderpearlCooldown.put(player.getName(), System.currentTimeMillis() + (HCF.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1_000L : 16_000L));
		}
	}

}
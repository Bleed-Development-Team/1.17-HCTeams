package net.frozenorb.foxtrot.gameplay.airdrops.listener;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.airdrops.command.AirDropCommand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class AirDropListener implements Listener {

    private HCF instance;

    public AirDropListener(HCF instance){
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItemInHand();

        if (event.isCancelled() || !AirDropCommand.isAirdrop(itemStack)) {
            return;
        }

        event.setCancelled(true);

        final Block block = event.getBlockPlaced();
        final AtomicInteger seconds = new AtomicInteger(4);

        //VoucherCommand.spawnFireworks(block.getLocation(), 1, 2, Color.RED, FireworkEffect.Type.BALL_LARGE);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        boolean airdropAll = itemStack.getItemMeta().getLore().size() > 3;

        player.updateInventory();

        new BukkitRunnable() {
            @Override
            public void run() {
                seconds.decrementAndGet();

                if (seconds.get() > 0) {
                    block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
                    block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);

                    player.sendMessage(ChatColor.RED + "Airdrop will drop in " + ChatColor.WHITE + seconds.get() + ChatColor.RED + ".");
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                    block.setType(Material.DROPPER);
                    block.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, block.getLocation(), 1, 0, 0, 0, 0);

                    HCF.getInstance().getAirDropHandler().setContents(block, player, airdropAll);

                    player.sendMessage(ChatColor.GREEN + "The Airdrop has dropped!");
                    this.cancel();

                    if (player.getWorld().getEnvironment() == World.Environment.NETHER || player.getWorld().getEnvironment() == World.Environment.THE_END || HCF.getInstance().getServerHandler().isWarzone(block.getLocation())) {
                        HCF.getInstance().getServer().getScheduler().runTaskLater(HCF.getInstance(), () -> {

                            if (block.getType() == Material.DROPPER) {
                                block.setType(Material.AIR);
                            }
                        }, 20 * 60 * 2);
                    }
                }
            }
        }.runTaskTimer(HCF.getInstance(), 0, 20);
    }
}

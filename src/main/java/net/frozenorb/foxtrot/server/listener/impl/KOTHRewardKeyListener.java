package net.frozenorb.foxtrot.server.listener.impl;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KOTHRewardKeyListener implements Listener {

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getItem() == null || event.getClickedBlock().getType() != Material.ENDER_CHEST || !DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation()) || !InventoryUtils.isSimilar(event.getItem(), ChatColor.RED + "KOTH Reward Key")) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST && DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
                openKothLoot(event.getPlayer(), event.getClickedBlock());
            }
            
            return;
        }

        event.setCancelled(true);

        int open = 0;

        for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                open++;
            }
        }

        if (open < 3) {
            event.getPlayer().sendMessage(ChatColor.RED + "You must have at least 3 open inventory slots to use a KOTH reward key!");
            return;
        }

        Block block = event.getClickedBlock().getRelative(BlockFace.DOWN, 3);

        if (block.getType() != Material.CHEST) {
            return;
        }

        ItemStack stack = event.getPlayer().getItemInHand();
        if (stack.getAmount() == 1) {
            event.getPlayer().setItemInHand(null);
        } else {
            stack.setAmount(stack.getAmount() - 1);
            event.getPlayer().setItemInHand(stack);
        }
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 1F);

        Chest chest = (Chest) block.getState();
        ItemStack[] lootTables = chest.getBlockInventory().getContents();
        final List<ItemStack> loot = new ArrayList<>();
        int given = 0;
        int tries = 0;
        int tier = NumberUtils.toInt(stack.getItemMeta().getLore().get(3).replaceAll("[^\\d.]", ""));

        toploop:
        while (given < 3 && tries < 100) {
            tries++;

            ItemStack chosenItem = lootTables[HCF.RANDOM.nextInt(27)];

            if (chosenItem == null || chosenItem.getType() == Material.AIR || chosenItem.getAmount() == 0) {
                continue;
            }

            for (ItemStack givenLoot : loot) {
                if (givenLoot.getType() == chosenItem.getType()) {
                    continue toploop; // 'continue's while loop, not for loop.
                }
            }

            given++;
            loot.add(chosenItem);
        }

        final StringBuilder builder = new StringBuilder();

        for (ItemStack itemStack : loot) {
            String displayName = itemStack.hasItemMeta() && Objects.requireNonNull(itemStack.getItemMeta()).hasDisplayName() ? ChatColor.RED.toString() + ChatColor.ITALIC + ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()) : ChatColor.BLUE.toString() + itemStack.getAmount() + "x " + ChatColor.YELLOW + WordUtils.capitalize(itemStack.getType().name().replace("_", " ").toLowerCase());

            builder.append(ChatColor.YELLOW).append(displayName).append(ChatColor.GOLD).append(", ");
        }

        if (builder.length() > 2) {
            builder.setLength(builder.length() - 2);
        }

        HCF.getInstance().getServer().broadcastMessage(CC.translate("&3&l[KOTH] ") + ChatColor.BLUE + event.getPlayer().getName() + ChatColor.WHITE + " is obtaining loot for a " + ChatColor.BLUE.toString() + ChatColor.ITALIC + "Tier " + tier + " key" + ChatColor.WHITE + " obtained from " + ChatColor.BLUE + InventoryUtils.getLoreData(event.getItem(), 1) + ChatColor.WHITE + " at " + ChatColor.BOLD + InventoryUtils.getLoreData(event.getItem(), 2) + ChatColor.WHITE + ".");

        new BukkitRunnable() {

            public void run() {
                new BukkitRunnable() {

                    public void run() {
                        HCF.getInstance().getServer().broadcastMessage(CC.translate("&3&l[KOTH] ") + ChatColor.BLUE + event.getPlayer().getName() + ChatColor.WHITE + " obtained " + builder.toString() + ChatColor.BLUE + "," + ChatColor.YELLOW + " from a " + ChatColor.BLUE.toString() + ChatColor.ITALIC + "KOTH key" + ChatColor.WHITE + ".");
                    }

                }.runTaskAsynchronously(HCF.getInstance());

                for (ItemStack lootItem : loot) {
                    event.getPlayer().getInventory().addItem(lootItem);
                }

                event.getPlayer().updateInventory();
            }

        }.runTaskLater(HCF.getInstance(), 20 * 5L);
    }
    
    private void openKothLoot(Player player, Block clickedBlock) {
        Block block = clickedBlock.getRelative(BlockFace.DOWN, 3);

        if (block.getType() != Material.CHEST) {
            return;
        }
        
        Chest chest = (Chest) block.getState();
        ItemStack[] lootTables = chest.getBlockInventory().getContents();
        
        Inventory inventory = Bukkit.createInventory(player, 27, "KOTH Loot");
        inventory.setContents(lootTables);
        
        player.openInventory(inventory);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (player.getOpenInventory() != null && player.getOpenInventory().getTitle() != null && player.getOpenInventory().getTitle().equals("KOTH Loot")) {
                event.setCancelled(true);
            }
        }
    }

}
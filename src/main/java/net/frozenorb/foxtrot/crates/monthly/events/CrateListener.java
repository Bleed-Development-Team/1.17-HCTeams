package net.frozenorb.foxtrot.crates.monthly.events;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.crates.monthly.animation.FinalAnimationService;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.crates.CrateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CrateListener implements Listener {
    public static HashMap<String, String> opening;
    public static HashMap<String, Integer> animationCounter;
    public static HashMap<String, Integer> animCount;
    public static HashMap<UUID, List<String>> rewards;
    public static ArrayList<Player> inStorageInv;

    @EventHandler
    public void inventoryClick(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inv = event.getView().getTopInventory();
        final int slot = event.getRawSlot();
        if (CrateListener.inStorageInv.contains(player)) {
            event.setCancelled(true);
        } else if (CrateListener.opening.containsKey(player.getName()) || CrateListener.opening.containsKey(player.getName() + "isStarting")) {
            event.setCancelled(true);
            String path;
            if (CrateListener.opening.containsKey(player.getName() + "isStarting")) {
                path = "crates." + CrateListener.opening.get(player.getName() + "isStarting") + ".gui.";
            } else {
                path = "crates." + CrateListener.opening.get(player.getName()) + ".gui.";
            }
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName() && CrateUtil.c(HCF.getInstance().getConfig().getString(path + "not-redeemed.Name")).equals(event.getCurrentItem().getItemMeta().getDisplayName())) {
                if (CrateListener.opening.containsKey(player.getName() + "isStarting")) {
                    player.sendMessage(ChatColor.GREEN + "You");
                    CrateListener.opening.put(player.getName(), CrateListener.opening.get(player.getName() + "isStarting"));
                    CrateListener.opening.remove(player.getName() + "isStarting");
                }
                if (slot != 49) {
                    CrateListener.animCount.put(player.getName() + ":" + HCF.getInstance().getConfig().getString(path + "animation.ScrambleAnimationRunsPerItem"), slot);
                } else {
                    String rewardPath = "crates." + CrateListener.opening.get(player.getName()) + ".final-rewards.";
                    rewardPath = rewardPath + "." + FinalAnimationService.generateReward(rewardPath) + ".";
                    final ItemStack finalReward = FinalAnimationService.item(player, rewardPath, true);

                    if (!CrateListener.rewards.containsKey(player.getUniqueId())) {
                        CrateListener.rewards.put(player.getUniqueId(), new ArrayList<>());
                    }
                    final List<String> lists = CrateListener.rewards.get(player.getUniqueId());

                    lists.add("JACKPOT:" + HCF.getInstance().getConfig().getString(rewardPath + "Name"));
                    CrateListener.rewards.put(player.getUniqueId(), lists);

                    inv.setItem(49, finalReward);

                    if (HCF.getInstance().getConfig().getBoolean(rewardPath + "GiveItem")) {
                        player.getInventory().addItem(finalReward);
                    }
                    for (final String command : HCF.getInstance().getConfig().getStringList(rewardPath + "Commands")) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && player.getItemInHand() != null) {
            final ItemStack hand = player.getItemInHand();
            if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
                for (final String key : HCF.getInstance().getConfig().getConfigurationSection("crates").getKeys(false)) {
                    if (hand.getItemMeta().getDisplayName().equals(CrateUtil.c(HCF.getInstance().getConfig().getString("crates." + key + ".crate.Name")))) {
                        if (player.getItemInHand().getAmount() == 1) {
                            player.setItemInHand(null);
                        } else {
                            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                        }

                        event.setCancelled(true);
                        rewards.put(player.getUniqueId(), new ArrayList<>());
                        CrateListener.opening.put(player.getName() + "isStarting", key);
                        player.openInventory(this.monthlyCrate(player.getName(), HCF.getInstance().getConfig().getString("crates." + key + ".gui.Name").replace("%name%", key)));
                    }
                }
            }
        }
    }

    @EventHandler
    public void inventoryCloseEvent(final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final Inventory inv = player.getOpenInventory().getTopInventory();
        CrateListener.inStorageInv.remove(player);
        if (CrateListener.opening.containsKey(player.getName() + "isStarting")) {
            CrateListener.opening.remove(player.getName() + "isStarting");
        } else if (CrateListener.opening.containsKey(player.getName())) {
            final String playerName = player.getName();
            final String name = inv.getItem(49).getItemMeta().getDisplayName();
            final String notPath = "crates." + CrateListener.opening.get(playerName) + ".gui.not-redeemed.";
            final String finalPath = "crates." + CrateListener.opening.get(playerName) + ".gui.final-not-redeemable.";
            if (name.equals(CrateUtil.c(HCF.getInstance().getConfig().getString(notPath + "Name"))) || name.equals(CrateUtil.c(HCF.getInstance().getConfig().getString(finalPath + "Name")))) {
                this.openInventory(player, inv);
            } else {
                final List<String> reward = rewards.remove(player.getUniqueId());

                if (CrateListener.opening.get(playerName).toLowerCase().contains("march")) {
                    for (Player onlinePlayer : HCF.getInstance().getServer().getOnlinePlayers()) {

                        onlinePlayer.sendMessage(CC.translate("&a&l&m------&2&l&m------&a&l&m------&2&l&m------&a&l&m------&2&l&m------&a&l&m------"));
                        onlinePlayer.sendMessage(CC.translate("  &a&ki&2&l March Chest &a&ki"));
                        onlinePlayer.sendMessage(CC.translate("  &fOpened by &a" + player.getName()));
                        onlinePlayer.sendMessage(CC.translate(""));
                        onlinePlayer.sendMessage(CC.translate("  &2&lREWARDS"));

                        String jackpot = "";

                        if (!reward.isEmpty()) {
                            for (String s : reward) {
                                if (s == null || s.equalsIgnoreCase("null")) {
                                    continue;
                                }

                                if (s.startsWith("JACKPOT:")) {
                                    jackpot = s;
                                    continue;
                                }

                                onlinePlayer.sendMessage("    " + CC.translate(s));
                            }
                        }

                        if (!jackpot.equals("")) {
                            onlinePlayer.sendMessage("");
                            onlinePlayer.sendMessage(CC.translate("  &2&lJACKPOT"));
                            onlinePlayer.sendMessage("    " + CC.translate(jackpot.replace("JACKPOT:", "")));
                        }

                        onlinePlayer.sendMessage(CC.translate(""));
                        onlinePlayer.sendMessage(CC.translate("  &fUnlocked at &ahttps://store.cavepvp.org/category/march-crates"));
                        onlinePlayer.sendMessage(CC.translate("&a&l&m------&2&l&m------&a&l&m------&2&l&m------&a&l&m------&2&l&m------&a&l&m------"));
                    }
                }

                CrateListener.opening.remove(player.getName());
            }
        }
    }

    public void openInventory(final Player player, final Inventory inv) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HCF.getInstance(), () -> player.openInventory(inv), 1L);
    }

    @EventHandler
    public void playerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        CrateListener.inStorageInv.remove(player);
        if (CrateListener.opening.containsKey(player.getName())) {
            CrateListener.opening.remove(player.getName());
            for (final String string : CrateListener.animationCounter.keySet()) {
                if (string.contains(player.getName())) {
                    CrateListener.animationCounter.remove(string);
                }
            }
        } else CrateListener.opening.remove(player.getName() + "isStarting");
    }

    public Inventory monthlyCrate(final String playerName, final String name) {
        final Inventory inv = Bukkit.createInventory(null, 54, CrateUtil.c(name));
        final String string = CrateListener.opening.get(playerName + "isStarting");
        final String notPath = "crates." + string + ".gui.not-redeemed.";
        final String finalPath = "crates." + string + ".gui.final-not-redeemable.";
        final String animPath = "crates." + string + ".gui.animation.animation-off-color";
        final String glowPath = "crates." + string + ".gui.animation.Glow";
        for (int i = 12; i < 33; i += 9) {
            for (int o = 0; o < 3; ++o) {
                inv.setItem(i + o, CrateUtil.createItemStack(Material.valueOf(HCF.getInstance().getConfig().getString(notPath + "Material")), 1, HCF.getInstance().getConfig().getString(notPath + "Name"), HCF.getInstance().getConfig().getBoolean(notPath + "Glow"), HCF.getInstance().getConfig().getInt(notPath + "ItemData"), HCF.getInstance().getConfig().getStringList(notPath + "Lores")));
            }
        }
        inv.setItem(49, CrateUtil.makeGUIPane(Material.valueOf(HCF.getInstance().getConfig().getString(finalPath + "Material")), 1, HCF.getInstance().getConfig().getString(finalPath + "Name"), HCF.getInstance().getConfig().getBoolean(finalPath + "Glow"), HCF.getInstance().getConfig().getStringList(finalPath + "Lores")));
        for (int i = 0; i < 54; ++i) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, CrateUtil.makeGUIPane(Material.valueOf(HCF.getInstance().getConfig().getString(animPath)), 1, " ", HCF.getInstance().getConfig().getBoolean(glowPath), null));
            }
        }
        return inv;
    }

    static {
        CrateListener.opening = new HashMap<>();
        CrateListener.rewards = new HashMap<>();
        CrateListener.animationCounter = new HashMap<>();
        CrateListener.animCount = new HashMap<>();
        CrateListener.inStorageInv = new ArrayList<>();
    }
}
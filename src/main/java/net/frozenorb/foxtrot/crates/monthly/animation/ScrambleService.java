package net.frozenorb.foxtrot.crates.monthly.animation;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.crates.monthly.events.CrateListener;
import net.frozenorb.foxtrot.util.crates.CrateUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ScrambleService extends BukkitRunnable {
    public HashMap<String, Material> currentColor;
    public HashMap<String, ArrayList<Material>> takenColors;
    public HashMap<String, Integer> timesRan;

    public ScrambleService() {
        this.currentColor = new HashMap<>();
        this.takenColors = new HashMap<>();
        this.timesRan = new HashMap<>();
    }

    public void run() {
        for (final String string : FinalAnimationService.finalAnimation.keySet()) {
            final Player player = Bukkit.getPlayer(string);
            final String name = player.getName();
            final Inventory inv = player.getOpenInventory().getTopInventory();
            final String[] split = FinalAnimationService.finalAnimation.get(string).split(":");
            final String apath = "crates." + CrateListener.opening.get(name) + ".gui.animation";
            Material color = null;
            if (!this.takenColors.containsKey(name)) {
                this.takenColors.put(name, new ArrayList<>());
            }
            for (int i = 0; i < 10000; ++i) {
                color = FinalAnimationService.dyeColor(apath);
                if (!this.takenColors.get(name).contains(color)) {
                    break;
                }
            }
            if (this.currentColor.containsKey(name)) {
                color = this.currentColor.get(name);
            } else {
                this.currentColor.put(name, color);
            }
            final ItemStack pane = CrateUtil.makeGUIPane(color, 1, " ", HCF.getInstance().getConfig().getBoolean(apath + ".Glow"), null);
            int left = Integer.parseInt(split[0]);
            int right = Integer.parseInt(split[1]);
            for (int j = 0; j < 6; ++j) {
                if (inv.getItem(left).hasItemMeta() && inv.getItem(left).getItemMeta().hasDisplayName() && inv.getItem(left).getItemMeta().getDisplayName().equals(" ")) {
                    inv.setItem(left, pane);
                }
                left += 9;
                if (inv.getItem(right).hasItemMeta() && inv.getItem(right).getItemMeta().hasDisplayName() && inv.getItem(right).getItemMeta().getDisplayName().equals(" ")) {
                    inv.setItem(right, pane);
                }
                right += 9;
            }
            left -= 54;
            right -= 54;
            FinalAnimationService.finalAnimation.put(string, left + 1 + ":" + (right - 1));
            if (left == 4 && right == 4) {
                FinalAnimationService.finalAnimation.put(string, "0:8");
                this.currentColor.remove(name);
                this.takenColors.get(name).add(color);
                if (!this.timesRan.containsKey(name)) {
                    this.timesRan.put(name, 1);
                } else {
                    this.timesRan.put(name, this.timesRan.get(name) + 1);
                }
                if (this.timesRan.get(name) != HCF.getInstance().getConfig().getInt(apath + ".FinalAnimationRuns")) {
                    continue;
                }
                this.takenColors.remove(name);
                this.timesRan.remove(name);
                FinalAnimationService.finalAnimation.remove(string);
                FinalAnimationService.changePanes(apath, player, false);
                final String notPath = "crates." + CrateListener.opening.get(name) + ".gui.not-redeemed.";
                inv.setItem(49, CrateUtil.createItemStack(Material.valueOf(HCF.getInstance().getConfig().getString(notPath + "Material")), 1, HCF.getInstance().getConfig().getString(notPath + "Name"), HCF.getInstance().getConfig().getBoolean(notPath + "Glow"), HCF.getInstance().getConfig().getInt(notPath + "ItemData"), HCF.getInstance().getConfig().getStringList(notPath + "Lores")));
            }
        }
    }
}

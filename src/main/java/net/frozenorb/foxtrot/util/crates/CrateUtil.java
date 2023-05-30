package net.frozenorb.foxtrot.util.crates;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.HCFConstants;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TODO: Write class description here
 *
 * @author Nopox
 * @project 1.17-HCTeams
 * @since 5/30/2023
 */
public class CrateUtil {

    public static void giveCrate(final String crateName, final Player player, final Integer amount, final CommandSender sender) {
        final List<String> lores = new ArrayList<>();
        for (final String string : HCF.getInstance().getConfig().getStringList("crates." + crateName + ".crate.Lores")) {
            lores.add(string.replace("%player%", player.getName()));
        }
        player.getInventory().addItem(createItemStack(Material.valueOf(HCF.getInstance().getConfig().getString("crates." + crateName + ".crate.Material")), amount, HCF.getInstance().getConfig().getString("crates." + crateName + ".crate.Name"), HCF.getInstance().getConfig().getBoolean("crates." + crateName + ".crate.Glow"), HCF.getInstance().getConfig().getInt("crates." + crateName + ".crate.ItemData"), lores));
        sender.sendMessage(CrateUtil.c(HCF.getInstance().getConfig().getString("messages.success-give").replace("%prefix%", HCFConstants.getPrefix()).replace("%crate%", crateName).replace("%amount%", Integer.toString(amount)).replace("%player%", player.getName())));
        player.sendMessage(CrateUtil.c(HCF.getInstance().getConfig().getString("messages.success-give-other").replace("%prefix%", HCFConstants.getPrefix()).replace("%crate%", crateName).replace("%amount%", Integer.toString(amount))));
    }

    public static ItemStack createItemStack(final Material type, final int amt, final String name, final boolean glow, final int data, final List<String> list) {
        ItemStack stack;

        if (data != -1) {
            stack = new ItemStack(type, amt, (short) data);
        } else {
            stack = new ItemStack(type, amt);
        }

        final ItemMeta im = stack.getItemMeta();

        if (name != null) {
            im.setDisplayName(c(name));
        }

        if (list != null) {
            final ArrayList<String> lore = new ArrayList<>();

            for (final String str : list) {
                lore.add(c(str));
            }

            im.setLore(lore);
        }

        stack.setItemMeta(im);

        if (glow) {
            glow(stack);
        }

        return stack;
    }

    public static ItemStack createItemStackSkull(final String playerName, final int amount, final String skullName, final List<String> lores) {
        final ItemStack stack = new ItemStack(Material.PLAYER_HEAD, amount, (short) 3);
        final SkullMeta im = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        im.setOwner(playerName);
        im.setDisplayName(c(skullName));
        if (lores != null) {
            final ArrayList<String> lore = new ArrayList<>();
            for (final String str : lores) {
                lore.add(c(str));
            }
            im.setLore(lore);
        }
        stack.setItemMeta(im);
        return stack;
    }

    public static int randInt(final int min, final int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static ItemStack makeGUIPane(final Material glasstype, final int amount, final String name, final boolean glow, final List<String> lore) {
        final ItemStack g = new ItemStack(glasstype, amount);
        final ItemMeta im = g.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        final ArrayList<String> lorelist = new ArrayList<>();
        if (lore != null) {
            for (String s : lore) {
                lorelist.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            im.setLore(lorelist);
        }
        g.setItemMeta(im);
        if (glow) {
            glow(g);
        }
        return g;
    }

    public static void glow(final ItemStack itemStack) {
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
        final ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        itemStack.setItemMeta(meta);
    }

    public static String c(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}

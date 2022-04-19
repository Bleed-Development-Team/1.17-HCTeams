package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.ExperienceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.Collections;

@CommandAlias("bottle")
@CommandPermission("foxtrot.bottle")
public final class BottleCommand extends BaseCommand implements Listener {

    public BottleCommand() {
        Bukkit.getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }
    

    @Default
    public void bottle(Player sender) {
        ItemStack item = sender.getInventory().getItemInMainHand();

        if (item.getType() != Material.GLASS_BOTTLE || item.getAmount() != 1) {
            sender.sendMessage(ChatColor.RED + "You must be holding one glass bottle in your hand.");
            return;
        }

        ExperienceManager manager = new ExperienceManager(sender);
        int experience = manager.getCurrentExp();
        manager.setExp(0.0D);

        if (experience == 0) {
            sender.sendMessage(ChatColor.RED + "You don't have any experience to bottle!");
            return;
        }

        ItemStack result = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = result.getItemMeta();
        assert meta != null;
        meta.setLore(Collections.singletonList(
                ChatColor.BLUE + "XP: " + ChatColor.WHITE + NumberFormat.getInstance().format(experience)
        ));
        result.setItemMeta(meta);

        sender.getInventory().setItemInMainHand(result);
        sender.sendMessage(ChatColor.GREEN + "You have bottled " + NumberFormat.getInstance().format(experience) + " XP!");
        sender.playSound(sender.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    }

}
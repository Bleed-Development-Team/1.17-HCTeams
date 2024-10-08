package net.frozenorb.foxtrot.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

@CommandAlias("lastinv")
@CommandPermission("foxtrot.lastinv")
public class LastInvCommand extends BaseCommand {


    @Default
    public static void lastInv(Player sender, @Flags("other") @Name("target")Player target) {
        HCF.getInstance().getServer().getScheduler().runTaskAsynchronously(HCF.getInstance(), () -> {
            HCF.getInstance().runRedisCommand((redis) -> {
                if (!redis.exists("lastInv:contents:" + target.toString())) {
                    sender.sendMessage(ChatColor.RED + "No last inventory recorded for " + FrozenUUIDCache.name(target.getUniqueId()));
                    return null;
                }

                ItemStack[] contents = HCF.PLAIN_GSON.fromJson(redis.get("lastInv:contents:" + target.getUniqueId().toString()), ItemStack[].class);
                ItemStack[] armor = HCF.PLAIN_GSON.fromJson(redis.get("lastInv:armorContents:" + target.getUniqueId().toString()), ItemStack[].class);

                cleanLoot(contents);
                cleanLoot(armor);

                HCF.getInstance().getServer().getScheduler().runTask(HCF.getInstance(), () -> {
                    sender.getInventory().setContents(contents);
                    sender.getInventory().setArmorContents(armor);
                    sender.updateInventory();

                    sender.sendMessage(ChatColor.GREEN + "Loaded " + FrozenUUIDCache.name(target.getUniqueId()) + "'s last inventory.");
                });

                return null;
            });
        });
    }

    public static void cleanLoot(ItemStack[] stack) {
        for (ItemStack item : stack) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                ItemMeta meta = item.getItemMeta();

                List<String> lore = item.getItemMeta().getLore();
                lore.remove(ChatColor.DARK_GRAY + "PVP Loot");
                meta.setLore(lore);

                item.setItemMeta(meta);
            }
        }
    }

    public static void recordInventory(Player player) {
        recordInventory(player.getUniqueId(), player.getInventory().getContents(), player.getInventory().getArmorContents());
    }

    public static void recordInventory(UUID player, ItemStack[] contents, ItemStack[] armor) {
        HCF.getInstance().getServer().getScheduler().runTaskAsynchronously(HCF.getInstance(), () -> {
            HCF.getInstance().runRedisCommand((redis) -> {
                redis.set("lastInv:contents:" + player.toString(), HCF.PLAIN_GSON.toJson(contents));
                redis.set("lastInv:armorContents:" + player.toString(), HCF.PLAIN_GSON.toJson(armor));
                return null;
            });
        });
    }

}
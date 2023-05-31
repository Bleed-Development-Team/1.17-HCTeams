package net.frozenorb.foxtrot.gameplay.clickable.type.potion;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.clickable.ClickableItem;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;

public class PotionMaker extends ClickableItem {

    @Override
    public String getID() {
        return "potionmaker";
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.BREWING_STAND)
                .name("&3&lPotion Maker")
                .addToLore("", "&3| &fRight click to start a",
                        "&3| &f5 second cooldown which gives you",
                        "&3| &f3 health potions",
                        "",
                        "&fObtainable through &bcrafting&f.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public String getCooldownID() {
        return "potionmaker";
    }

    @Override
    public void handle(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        event.setCancelled(true);

        if (SpawnTagHandler.isTagged(player)){
            player.sendMessage(CC.translate("&cYou cannot use a potion maker in combat."));
            return;
        }

        takeItem(player);
        giveCooldown(player);

        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&3&lPotion Maker"));
        player.sendMessage(CC.translate("&fYou have used your potion maker!"));
        player.sendMessage(CC.translate("&7Please wait 5 seconds for your potions to brew."));
        player.sendMessage(CC.translate(""));

        HCF.getInstance().getServer().getScheduler().runTaskLater(HCF.getInstance(), () -> {
            player.sendMessage(CC.translate(""));
            player.sendMessage(CC.translate("&3&lPotion Maker"));
            player.sendMessage(CC.translate("&fYour potions are done brewing!"));
            player.sendMessage(CC.translate("&7You have received 3 health potions."));
            player.sendMessage(CC.translate(""));

            for (int i = 0; i < 3; i++){
                PotionData potionData = new PotionData(PotionType.INSTANT_HEAL, false, false);
                Potion potion = new Potion(potionData.getType());
                potion.setSplash(true);
                potion.setLevel(2);

                ItemStack potionItem = potion.toItemStack(1);
                PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
                potionMeta.setDisplayName(CC.translate("&fSplash Potion of Healing"));
                potionItem.setItemMeta(potionMeta);

                player.getInventory().addItem(potionItem);
            }
        }, 20 * 5);
    }
}

package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LuckyModeAbility extends Ability implements Listener {


    @Override
    public String getName() {
        return "&e&lLucky Mode";
    }

    @Override
    public String getUncoloredName() {
        return "Lucky Mode";
    }

    @Override
    public String getDescription() {
        return "Right click to have a chance of either having Strength II or Weakness II.";
    }

    @Override
    public String getCooldownID() {
        return "lucky";
    }

    @Override
    public int getCooldown() {
        return 60 * 2;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getLuckyMode();
    }

    @EventHandler
    public void use(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (!isSimilarTo(player.getItemInHand(), Items.getLuckyMode())) return;
            if (!canUse(player)) return;

            int chance = Foxtrot.RANDOM.nextInt(0, 2);

            if (chance == 1){
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 1));

                player.sendMessage(CC.translate("&c❤ &6You were unlucky and got &fWeakness II&6!"));
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1));

                player.sendMessage(CC.translate("&c❤ &6You were lucky and got &fStrength II&6!"));
            }

            giveCooldowns(player);
            useItem(player);
        }
    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getLuckyMode())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&c's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            }
        }
    }

}

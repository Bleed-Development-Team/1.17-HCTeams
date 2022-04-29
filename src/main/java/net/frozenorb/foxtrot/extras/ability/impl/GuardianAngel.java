package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GuardianAngel extends Ability implements Listener {
    @Override
    public String getName() {
        return "&6&lGuardian Angel";
    }

    @Override
    public String getUncoloredName() {
        return "Guardian Angel";
    }

    @Override
    public String getDescription() {
        return "If you go under 3 hearts, you will be healed to full health.";
    }

    @Override
    public String getCooldownID() {
        return "guardian";
    }

    @Override
    public int getCooldown() {
        return 60 * 2;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getGuardian();
    }

    @EventHandler
    public void rightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!isSimilarTo(player.getItemInHand(), getItemStack())) return;
            if (!canUse(player)) return;


            applyCust(player, "You have activated the &6Guardian Angel&6!", true);

            Cooldown.addCooldown("guardian-effect", player, getCooldown());

            if (player.getHealth() <= 3) {
                player.setHealth(player.getMaxHealth());

                player.sendMessage(CC.translate(""));
                player.sendMessage(CC.translate("&c❤ &6The &fGuardian Angel &6has activated"));
                player.sendMessage(CC.translate("&c❤ &6You have been healed to full health"));
                player.sendMessage(CC.translate(""));

                return;
            }
        }
    }
}

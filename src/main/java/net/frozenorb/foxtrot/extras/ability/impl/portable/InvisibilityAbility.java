package net.frozenorb.foxtrot.extras.ability.impl.portable;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class InvisibilityAbility extends Ability {
    @Override
    public String getName() {
        return "&7Invisibility";
    }

    @Override
    public String getUncoloredName() {
        return "Invisibility";
    }

    @Override
    public String getDescription() {
        return "Right click to receive 43 seconds of Invisibility.";
    }

    @Override
    public String getCooldownID() {
        return "portable";
    }

    @Override
    public int getCooldown() {
        return 43;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getInvis();
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (!isSimilarTo(player.getItemInHand(), Items.getInvis())) return;
            if (!canUse(player)) return;

            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 43, 0));

            giveCooldowns(player);
            useItem(player);

            player.sendMessage(CC.translate("&c❤ &6You have gave yourself &fStrength II&6."));
        }
    }
}

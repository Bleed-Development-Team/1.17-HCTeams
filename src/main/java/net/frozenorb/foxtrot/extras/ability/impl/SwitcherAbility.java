package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SwitcherAbility extends Ability implements Listener {
    @Override
    public String getName() {
        return "&f&lSwitcher";
    }

    @Override
    public String getUncoloredName() {
        return "Switcher";
    }

    @Override
    public String getDescription() {
        return "Right click to switch teleport to the player's position.";
    }

    @Override
    public String getCooldownID() {
        return "switcher";
    }

    @Override
    public int getCooldown() {
        return 16;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getSnowball();
    }

    @EventHandler
    public void entity(EntityDamageByEntityEvent event){
        if (!(event.getEntity() instanceof Player)) return;
        Player victim = (Player) event.getEntity();

        if (event.getDamager() instanceof Snowball){
            Snowball snowball = (Snowball) event.getDamager();

            if (snowball.getShooter() instanceof Player){

                if (((Player) snowball.getShooter()).getLocation().distance(victim.getLocation()) > 8){
                    ((Player) snowball.getShooter()).sendMessage(CC.translate("&cYou cannot switcher people from more than 8 blocks away!"));
                    return;
                }

                Location f1 = victim.getLocation();
                Location f2 = ((Player) snowball.getShooter()).getLocation();

                victim.teleport(f2);
                ((Player) snowball.getShooter()).teleport(f1);

                applyDir((Player) snowball.getShooter(), victim);
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Player player = event.getPlayer();

            if (!isSimilarTo(player.getItemInHand(), Items.getSnowball())) return;

            if (!canUse(player)){
                event.setCancelled(true);
                return;
            }

            giveCooldowns(player);
        }
    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getSnowball())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&c's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            }
        }
    }


}

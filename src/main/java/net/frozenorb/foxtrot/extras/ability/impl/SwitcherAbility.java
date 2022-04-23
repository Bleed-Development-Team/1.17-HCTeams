package net.frozenorb.foxtrot.extras.ability.impl;

import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Location;
import org.bukkit.Material;
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
        Player victim = (Player) event.getEntity();

        if (event.getDamager() instanceof Snowball){
            Snowball snowball = (Snowball) event.getDamager();

            if (snowball.getShooter() instanceof Player){

                if (event.isCancelled()) return;

                if (isOnGlobalCooldown(((Player) snowball.getShooter()))){

                    ((Player) snowball.getShooter()).sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(((Player) snowball.getShooter()),"partner") + "&c."));
                    return;
                }

                if (isOnCooldown(((Player) snowball.getShooter()))){
                    //event.setCancelled(true);

                    ((Player) snowball.getShooter()).getInventory().addItem(Items.getSnowball());
                    ((Player) snowball.getShooter()).updateInventory();

                    ((Player) snowball.getShooter()).sendMessage(CC.translate("&cYou are on cooldown for the &f&lSwitcher &cfor another &c&l" + getCooldownFormatted(((Player) snowball.getShooter())) + "&c."));
                    return;
                }

                if (DTRBitmask.SAFE_ZONE.appliesAt(victim.getLocation())){
                    giveCooldowns(victim);
                    ((Player) snowball.getShooter()).sendMessage(CC.translate("&cYou cannot switcher people inside of &a&lSpawn&c."));
                    return;
                }

                if (DTRBitmask.KOTH.appliesAt(victim.getLocation())){
                    giveCooldowns(victim);
                    ((Player) snowball.getShooter()).sendMessage(CC.translate("&cYou cannot switcher people inside of &b&lKOTH&c."));
                    return;
                }

                if (DTRBitmask.CITADEL.appliesAt(victim.getLocation())){
                    giveCooldowns(victim);
                    ((Player) snowball.getShooter()).sendMessage(CC.translate("&cYou cannot switcher people inside of &5&lCitadel&c."));
                    return;
                }

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
    public void interact(ProjectileLaunchEvent event){
        Player player = event.getEntity().getShooter() instanceof Player ? (Player) event.getEntity().getShooter() : null;

        if (event.getEntity() instanceof Snowball && event.getEntity() == Items.getSnowball()){

            if (isOnGlobalCooldown(player)){
                event.setCancelled(true);
                assert player != null;
                player.updateInventory();

                player.sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(player,"partner") + "&c."));
                event.setCancelled(true);
                return;
            }

            if (isOnCooldown(player)){
                event.setCancelled(true);
                player.updateInventory();

                player.sendMessage(CC.translate("&cYou are on cooldown for the &f&lSwitcher &cfor another &c&l" + getCooldownFormatted(player) + "&c."));
            }
        }
    }

    @EventHandler
    public void cooldownCheck(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!isSimilarTo(player.getItemInHand(), Items.getSnowball())) return;

            if (isOnCooldown(player)){
                player.sendMessage(CC.translate("&cYou are on the " + getName() + "&6's cooldown for another &c&l" + getCooldownFormatted(player) + "&c."));
            }
        }
    }


}

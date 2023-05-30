package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import net.frozenorb.foxtrot.gameplay.ability.damage.DamageAbility;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Switcher extends InteractAbility {
    @Override
    public String getID() {
        return "switcher";
    }

    @Override
    public String getName() {
        return "&d&lSwitcher";
    }

    @Override
    public int getCooldown() {
        return 15;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.SNOWBALL)
                .name(getName())
                .addToLore("&7Hit someone to switch places with them.")
                .build();
    }

    @Override
    public String getColor() {
        return "&d";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        addCooldown(player);
    }

    @EventHandler
    public void entity(ProjectileHitEvent event){
        if (!(event.getHitEntity() instanceof Player victim)) return;

        if (event.getEntity() instanceof Snowball snowball){

            if (snowball.getShooter() instanceof Player){
                if (!victimCheck((Player) snowball.getShooter(), victim)) return;

                if (((Player) snowball.getShooter()).getLocation().distance(victim.getLocation()) > 8){
                    ((Player) snowball.getShooter()).sendMessage(CC.translate("&cYou cannot switcher people from more than 8 blocks away!"));
                    return;
                }

                Location f1 = victim.getLocation();
                Location f2 = ((Player) snowball.getShooter()).getLocation();

                victim.teleport(f2);
                ((Player) snowball.getShooter()).teleport(f1);

                sendMessage(((Player) snowball.getShooter()),"You have swapped places with " + getColor() + victim.getName() + "&f.");
                victim.sendMessage(CC.translate("&cYour places have been swapped with " + ((Player) snowball.getShooter()).getName() + "&c!"));
            }
        }
    }
}

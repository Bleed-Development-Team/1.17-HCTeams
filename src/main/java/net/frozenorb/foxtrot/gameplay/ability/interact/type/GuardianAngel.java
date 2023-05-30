package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.damage.DamageAbility;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GuardianAngel extends InteractAbility {

    private List<Player> guardian = new ArrayList<>();

    public GuardianAngel(){
        Cooldown.createCooldown("guardian-effect");

        Bukkit.getScheduler().runTaskTimer(HCF.getInstance(), () -> {
            for (Player player : guardian){
                if (!Cooldown.isOnCooldown("guardian-effect", player)){
                    Cooldown.removeCooldown("guardian-effect", player);
                    continue;
                }
                if (player.getHealth() <= 3){
                    heal(player);
                }
            }
        }, 0, 15L);
    }

    @Override
    public String getID() {
        return "guardian-angel";
    }

    @Override
    public String getName() {
        return "&6&lGuardian Angel";
    }

    @Override
    public int getCooldown() {
        return 120;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.CLOCK)
                .name(getName())
                .addToLore("&7Right click to heal yourself if you go under 3 hearts.")
                .build();
    }

    @Override
    public String getColor() {
        return "&6";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        addCooldown(player);

        Cooldown.addCooldown("guardian-effect", player, getCooldown());
        guardian.add(player);

        use(player);

        if (player.getHealth() <= 3) {
            heal(player);
        }
    }

    private void heal(Player player){
        player.setHealth(player.getMaxHealth());

        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&6&lGuardian Angel"));
        player.sendMessage(CC.translate("&6| &fThe &6Guardian Angel &fhas activated!"));
        player.sendMessage(CC.translate("&6| &fYou have been restored to full health!"));
        player.sendMessage(CC.translate(""));

        guardian.remove(player);
        Cooldown.removeCooldown("guardian-effect", player);
    }
}

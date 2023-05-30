package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Combo extends InteractAbility {
    Map<UUID, Integer> combos = new HashMap<>();

    public Combo(){
        Cooldown.createCooldown("combo-eff");
    }

    @Override
    public String getID() {
        return "combo";
    }

    @Override
    public String getName() {
        return "&6&lCombo Ability";
    }

    @Override
    public int getCooldown() {
        return 60 * 2;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.PUFFERFISH)
                .name("&6&lCombo Ability")
                .addToLore("&7Receive a second for Strength II for every hit.")
                .build();
    }

    @Override
    public String getColor() {
        return "&6";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        combos.put(player.getUniqueId(), combos.getOrDefault(player.getUniqueId(), 0) + 1);
        Cooldown.addCooldown("combo-eff", player, 10);

        sendMessage(player, "Receive a second for Strength II for every hit.");

        use(player);

        event.setCancelled(true);

        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (20 * combos.get(player.getUniqueId())), 1));
            player.sendMessage("");
            player.sendMessage(CC.translate("&b&lCombo Ability"));
            player.sendMessage(CC.translate("&b&l| &fYou have received &6" + combos.get(player.getUniqueId()) + " &fsecond" + (combos.get(player.getUniqueId()) == 1 ? "" : "s") + " of &cStrength II&f."));
            player.sendMessage("");

            combos.remove(player.getUniqueId());
        }, 20L * 10L);
    }


    @EventHandler(ignoreCancelled = true)
    public void hitPlayer(EntityDamageByEntityEvent event){
        if (!(event.getEntity() instanceof Player) || (!(event.getDamager() instanceof Player damager))) return;

        Player victim = (Player) event.getEntity();

        if (Cooldown.isOnCooldown("combo-eff", damager)){
            if (combos.containsKey(damager.getUniqueId()) && combos.get(damager.getUniqueId()) < 10){
                if (!canUse(damager)) return;

                combos.put(damager.getUniqueId(), combos.get(damager.getUniqueId()) + 1);
            }
        }
    }
}

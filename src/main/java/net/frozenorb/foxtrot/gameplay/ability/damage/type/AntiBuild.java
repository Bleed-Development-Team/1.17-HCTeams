package net.frozenorb.foxtrot.gameplay.ability.damage.type;

import net.frozenorb.foxtrot.gameplay.ability.damage.DamageAbility;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AntiBuild extends DamageAbility {
    Map<UUID, Map<UUID, Integer>> hitMap = new HashMap<>();

    public AntiBuild(){
        Cooldown.createCooldown("bone-eff");
    }

    @Override
    public String getID() {
        return "antibuild";
    }

    @Override
    public String getName() {
        return "&6&lAnti-Build Stick";
    }

    @Override
    public int getCooldown() {
        return 120;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.BONE)
                .name("&6&lAnti-Build Stick")
                .addToLore("&7Hit a player 3 times with this ability", "&7to prevent them from placing and breaking blocks.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public String getColor() {
        return "&6";
    }

    @Override
    public void handle(EntityDamageByEntityEvent event, Player damager, Player victim) {
        int hits;

        if (hitMap.containsKey(damager.getUniqueId()) && hitMap.get(damager.getUniqueId()).containsKey(victim.getUniqueId())) {
            hits = hitMap.get(damager.getUniqueId()).get(victim.getUniqueId());
        } else {
            hits = 1;
        }

        if (hits < 3){
            Map<UUID, Integer> victimMap = new HashMap<>();
            victimMap.put(victim.getUniqueId(), hits + 1);
            hitMap.put(damager.getUniqueId(), victimMap);

            damager.sendMessage(CC.translate("&fYou need to hit &b" + victim.getName() + " &r&f" + (3 - hits) + " &fmore time" + (3 - hits == 1 ? "" : "s") + "."));
            return;
        }

        hitMap.remove(damager.getUniqueId());

        Cooldown.addCooldown("bone-eff", victim, 15);

        use(damager);
        sendMessage(damager, "&6" + victim.getName() + " &fwill not be able to place and break blocks for 10 seconds.");
    }


    @EventHandler
    public void place(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if (Cooldown.isOnCooldown("bone-eff", player)){
            event.setCancelled(true);

            player.sendMessage(CC.translate("&cYou cannot place blocks for another &c&l" + Cooldown.getCooldownString(player, "bone-eff") + "&c."));
        }
    }

    @EventHandler
    public void breakb(BlockBreakEvent event){
        Player player = event.getPlayer();

        if (Cooldown.isOnCooldown("bone-eff", player)){
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou cannot break blocks for another &c&l" + Cooldown.getCooldownString(player, "bone-eff") + "&c."));
        }
    }

    @EventHandler
    public void inte(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction().name().contains("RIGHT") && !event.getAction().name().contains("AIR")) {
            if (event.getClickedBlock().getType() == Material.OAK_FENCE_GATE || event.getClickedBlock().getType() == Material.SPRUCE_FENCE_GATE || event.getClickedBlock().getType() == Material.BIRCH_FENCE_GATE || event.getClickedBlock().getType() == Material.JUNGLE_FENCE_GATE || event.getClickedBlock().getType() == Material.DARK_OAK_FENCE_GATE || event.getClickedBlock().getType() == Material.ACACIA_FENCE_GATE || event.getClickedBlock().getType() == Material.OAK_DOOR) {
                if (Cooldown.isOnCooldown("bone-eff", player)){
                    event.setUseInteractedBlock(Event.Result.DENY);

                    player.sendMessage(CC.translate("&cYou cannot interact with blocks for another &c&l" + Cooldown.getCooldownString(player, "bone-eff") + "&c."));
                }
            } else if (event.getClickedBlock().getType() == Material.CHEST){
                if (Cooldown.isOnCooldown("bone-eff", player)){
                    event.setCancelled(true);

                    player.sendMessage(CC.translate("&cYou cannot interact with chests for another &c&l" + Cooldown.getCooldownString(player, "bone-eff") + "&c."));
                }
            }
        }
    }
}

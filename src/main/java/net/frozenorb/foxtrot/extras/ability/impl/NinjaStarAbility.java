package net.frozenorb.foxtrot.extras.ability.impl;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NinjaStarAbility extends Ability implements Listener {
    private Map<UUID, UUID> hits = new HashMap<>();


    @Override
    public String getName() {
        return "&b&lNinja Star";
    }

    @Override
    public String getUncoloredName() {
        return "Ninja Star";
    }

    @Override
    public String getDescription() {
        return "Teleports you to the last person who hit you (Within 15 seconds!)";
    }

    @Override
    public String getCooldownID() {
        return "ninja";
    }

    @Override
    public int getCooldown() {
        return 2 * 60;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getNinjaStar();
    }


    @EventHandler
    public void rightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!isSimilarTo(player.getItemInHand(), Items.getNinjaStar())) return;
            if (!canUse(player)) return;

            if (!hits.containsKey(player.getUniqueId())) {
                player.sendMessage("&c❤ &6Failed to use: &fNo last hit.");
                return;
            }

            if (!SpawnTagHandler.isTagged(player) || SpawnTagHandler.getTag(player) < 15) {
                hits.remove(player.getUniqueId());

                player.sendMessage("&c❤ &6Failed to use: &fNo last hit.");
                return;
            }

            Player victim = Bukkit.getPlayer(hits.get(player.getUniqueId()));

            if (victim.isOnline()){

                new BukkitRunnable(){
                    int i = 0;
                    @Override
                    public void run() {
                        if (i == 0){
                            player.sendMessage(CC.translate("&c❤ &6You will teleport to &f" + victim.getName() + " &6in &f3 &6seconds."));
                            victim.sendMessage(CC.translate("&c❤ &f" + player.getName() + " &6will teleport to you in &f3 &6seconds."));

                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                            victim.playSound(victim.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                        } else if (i == 1){
                            player.sendMessage(CC.translate("&c❤ &6You will teleport to &f" + victim.getName() + " &6in &f2 &6seconds."));
                            victim.sendMessage(CC.translate("&c❤ &f" + player.getName() + " &6will teleport to you in &f2 &6seconds."));

                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                            victim.playSound(victim.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                        } else if (i == 2){
                            player.sendMessage(CC.translate("&c❤ &6You will teleport to &f" + victim.getName() + " &6in &f1 &6seconds."));
                            victim.sendMessage(CC.translate("&c❤ &f" + player.getName() + " &6will teleport to you in &f1 &6seconds."));

                            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1F, 1F);
                            victim.playSound(victim.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1F, 1F);
                        } else if (i == 3){
                            player.teleport(victim);

                            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1F, 1F);
                            victim.playSound(victim.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1F, 1F);

                            cancel();
                        }

                        i++;
                    }
                }.runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
            }
        }
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player player){

            if (hits.containsKey(player.getUniqueId())) return;

            hits.put(player.getUniqueId(), player.getUniqueId());

            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                hits.remove(player.getUniqueId());
            }, 20L * 15L);
        }
    }
}

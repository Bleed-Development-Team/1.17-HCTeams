package net.frozenorb.foxtrot.extras.ability.impl;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NinjaStarAbility extends Ability implements Listener {
    @Getter private Map<UUID, UUID> lastHits = new HashMap<>();
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
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!isSimilarTo(player.getItemInHand(), getItemStack())) return;
        if (!canUse(player)) return;

        if (!getLastHits().containsKey(player.getUniqueId()) || !SpawnTagHandler.isTagged(player) | getSpawnTagScore(player) > 15) {
            player.sendMessage(CC.translate("&c❤ &6No last hit found!"));
            return;
        }
        Player target = Bukkit.getPlayer(getLastHits().get(player.getUniqueId()));
        if (target == null) {
            player.sendMessage(CC.translate("&c❤ &6No last hit found!"));
            return;
        }
        giveCooldowns(player);
        player.sendMessage(CC.translate("&c❤ &6You have used a &fNinja Ability&6!"));
        player.sendMessage(CC.translate("&c❤ &6Teleporting to &f" + target.getName() + " &6in 3 seconds..."));
        target.sendMessage(CC.translate("&c❤ &f" + player.getName() + " &6is teleporting to you in 3 seconds..."));
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            player.sendMessage(CC.translate("&c❤ &6Teleporting to  &f" + target.getName() + " &6in 2 seconds..."));
            target.sendMessage(CC.translate("&c❤ &f" + player.getName() + " &6is teleporting to you in 2 seconds..."));
        }, 20L);
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            player.sendMessage(CC.translate("&c❤ &6Teleporting to  &f" + target.getName() + " &6in 1 seconds..."));
            target.sendMessage(CC.translate("&c❤ &f" + player.getName() + " &6is teleporting to you in 1 seconds..."));
        }, 20L * 2);
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            player.teleport(target);
            player.sendMessage(CC.translate("&c❤ &6Teleported to &f" + target.getName() + "&6!"));
            target.sendMessage(CC.translate("&c❤ &f" + player.getName() + " &6has teleported to you!"));
        }, 20L * 3);

        getLastHits().remove(player.getUniqueId());

    }


    @EventHandler
    public void addToList(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (getLastHits().containsKey(victim.getUniqueId())) {
            getLastHits().remove(victim.getUniqueId());
            getLastHits().put(victim.getUniqueId(), player.getUniqueId());
            return;
        }
        getLastHits().put(victim.getUniqueId(), player.getUniqueId());
    }
    public float getSpawnTagScore(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            float diff = SpawnTagHandler.getTag(player);

            if (diff >= 0) {
                return diff;
            }
        }

        return 0;
    }
}

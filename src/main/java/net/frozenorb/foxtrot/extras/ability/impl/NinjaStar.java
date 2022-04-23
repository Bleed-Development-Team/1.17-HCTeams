package net.frozenorb.foxtrot.extras.ability.impl;

import lombok.Getter;
import net.frozenorb.foxtrot.extras.ability.Ability;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NinjaStar extends Ability implements Listener {
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
        return null;
    }

    //@EventHandler
    //public void addToList(EntityDamageByEntityEvent event)
}

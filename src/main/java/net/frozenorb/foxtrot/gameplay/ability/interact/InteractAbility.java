package net.frozenorb.foxtrot.gameplay.ability.interact;

import net.frozenorb.foxtrot.gameplay.ability.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class InteractAbility extends Ability {

    public abstract void handle(PlayerInteractEvent event, Player player);
}

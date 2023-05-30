package net.frozenorb.foxtrot.server.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;


public class PlayerPearlRefundEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public PlayerPearlRefundEvent(Player player) {
        super(player);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

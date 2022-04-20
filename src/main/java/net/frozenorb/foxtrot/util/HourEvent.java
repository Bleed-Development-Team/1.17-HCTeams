package net.frozenorb.foxtrot.util;

import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class HourEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final int hour;

    public HandlerList getHandlers() {
        return handlerList;
    }


    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public int getHour() {
        return this.hour;
    }
}

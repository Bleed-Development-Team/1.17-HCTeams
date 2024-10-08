package net.frozenorb.foxtrot.gameplay.events.citadel.events;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CitadelCapturedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private ObjectId capper;

    public CitadelCapturedEvent(ObjectId capper) {
        this.capper = capper;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
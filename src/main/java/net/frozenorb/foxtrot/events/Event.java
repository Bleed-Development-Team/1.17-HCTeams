package net.frozenorb.foxtrot.events;

public interface Event {
    String getName();
    boolean isActive();
    void tick();
    void setActive(boolean active);
    boolean isHidden();
    void setHidden(boolean hidden);
    boolean activate();
    void deactivate();

    EventType getType();
}

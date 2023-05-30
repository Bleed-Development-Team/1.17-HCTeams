package net.frozenorb.foxtrot.provider.nametags.packet;

import lombok.Getter;
import net.frozenorb.foxtrot.provider.nametags.extra.NameVisibility;
import org.bukkit.entity.Player;

@Getter
public abstract class NametagPacket {
    protected Player player;
    
    public abstract void addToTeam(Player player, String team);
    
    public NametagPacket(Player player) {
        this.player = player;
    }
    
    public abstract void create(String name, String color, String prefix, String suffix, boolean friendlyInvis, NameVisibility visibilitt);
}

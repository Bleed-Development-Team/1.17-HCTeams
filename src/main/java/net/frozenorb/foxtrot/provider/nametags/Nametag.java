package net.frozenorb.foxtrot.provider.nametags;

import lombok.Getter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.provider.nametags.packet.NametagPacket;
import org.bukkit.entity.Player;

public class Nametag {
    private final Player player;

    public Player getPlayer() {
        return player;
    }

    public NametagPacket getPacket() {
        return packet;
    }

    private final NametagPacket packet;
    
    public Nametag(Player player) {
        this.player = player;
        this.packet = HCF.getInstance().getNametagManager().createPacket(player);
    }
}

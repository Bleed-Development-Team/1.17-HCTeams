package net.frozenorb.foxtrot.provider.nametags;

import lombok.Getter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.provider.nametags.packet.NametagPacket;
import org.bukkit.entity.Player;

@Getter
public class Nametag {
    private final Player player;
    private final NametagPacket packet;
    
    public Nametag(Player player) {
        this.player = player;
        this.packet = HCF.getInstance().getNametagManager().createPacket(player);
    }
}

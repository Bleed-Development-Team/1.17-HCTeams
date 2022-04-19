package net.frozenorb.foxtrot.team.commands.team.subclaim;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import org.bukkit.entity.Player;

public class TeamSubclaimMapCommand {

    @Command(value ={ "team subclaim map", "t subclaim map", "f subclaim map", "faction subclaim map", "fac subclaim map", "team sub map", "t sub map", "f sub map", "faction sub map", "fac sub map" })
    public static void teamSubclaimMap(@Sender Player sender) {
        (new VisualClaim(sender, VisualClaimType.SUBCLAIM_MAP, false)).draw(false);
    }

}
package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import org.bukkit.entity.Player;

public class TeamMapCommand {

    @Command(value={ "team map", "t map", "f map", "faction map", "fac map", "map" })
    public static void teamMap(@Sender Player sender) {
        (new VisualClaim(sender, VisualClaimType.MAP, false)).draw(false);
    }

//    @Command(names={ "team map surface", "t map surface", "f map surface", "faction map surface", "fac map surface", "map surface" }, permission="")
//    public static void teamMapSurface(Player sender) {
//        (new VisualClaim(sender, VisualClaimType.SURFACE_MAP, false)).draw(false);
//    }

}
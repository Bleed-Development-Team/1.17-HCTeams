package net.frozenorb.foxtrot.provider.nametags.adapter;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.provider.nametags.Nametag;
import net.frozenorb.foxtrot.provider.nametags.NametagAdapter;
import net.frozenorb.foxtrot.provider.nametags.extra.NameVisibility;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

public class FoxtrotNametags implements NametagAdapter {
    
    private String createTeam(Player from, Player to, String id, String prefix, String suffix, NameVisibility visibility) {
        Nametag nametag = HCF.getInstance().getNametagManager().getNametags().get(from.getUniqueId());
        String displayName = prefix.isEmpty() ? "" : prefix + " ";
        if (nametag != null) {
            nametag.getPacket().create(id, suffix, CC.translate(displayName), CC.translate(suffix), true, visibility);
            nametag.getPacket().addToTeam(to, id);
        }
        return displayName + suffix;
    }

    private String createTeam(Player from, Player to, String id, String prefix, String suffix) {
        return this.createTeam(from, to, id, prefix, suffix, NameVisibility.ALWAYS);
    }

    @Override
    public String getAndUpdate(Player from, Player to) {
        Team team = HCF.getInstance().getTeamHandler().getTeam(from.getUniqueId());
        Team teamTo = HCF.getInstance().getTeamHandler().getTeam(to.getUniqueId());

        if (team != null && team.isMember(to.getUniqueId()) || from == to){
            return this.createTeam(from, to, "team", "", "&2");
        }
        if (team != null && teamTo != null && team.isAlly(teamTo)){
            return this.createTeam(from, to, "ally", "", "&b");
        }
        if (ArcherClass.isMarked(to)){
            return this.createTeam(from, to, "archer", "", "&e");
        }
        if (HCF.getInstance().getPvPTimerMap().hasTimer(to.getUniqueId())){
            return this.createTeam(from, to, "pvptimer", "", "&a");
        }

        return this.createTeam(from, to, "enemy", "", "&c");
    }
}

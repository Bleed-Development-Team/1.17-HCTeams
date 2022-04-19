package net.frozenorb.foxtrot.nametag;

import com.lunarclient.bukkitapi.LunarClientAPI;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClientNametagProvider implements Runnable{

    @Override
    public void run() {
        List<String> lines = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()){
            Bukkit.getOnlinePlayers().forEach(viewer -> LunarClientAPI.getInstance().overrideNametag(player, getNametag(player, viewer), viewer));
        }
    }

    public List<String> getNametag(Player player, Player viewer){
        List<String> lines = new ArrayList<>();
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team != null){
            lines.add(CC.translate("&6[" + (team.isMember(viewer.getUniqueId()) ? "&a" : "&c") + team.getName() + " &7" + CC.VERTICAL_SEPARATOR + " &r" + team.getFormattedDTR() + "&6]"));
        }

        return lines;
    }
}

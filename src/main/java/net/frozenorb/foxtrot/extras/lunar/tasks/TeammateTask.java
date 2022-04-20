package net.frozenorb.foxtrot.extras.lunar.tasks;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeammateTask  {


    public TeammateTask(){
        Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            for (Player viewer : Bukkit.getOnlinePlayers()){
                Team team = Foxtrot.getInstance().getTeamHandler().getTeam(viewer);

                if (team == null) continue;

                for (Player players : team.getOnlineMembers()){
                    LunarClientAPI.getInstance().sendTeammates(players, new LCPacketTeammates(team.getOwner(), players.getPing(), getOnlineTeammates(players.getWorld(), team)));
                }
            }
        }, 0L, 20L);
    }

    public Map<UUID, Map<String, Double>> getOnlineTeammates(World world, Team team) {
        Map<UUID, Map<String, Double>> playersMap = new HashMap<>();
        team.getOnlineMembers().forEach(teammate -> {
            if (teammate.getWorld().equals(world) && !teammate.isDead()) {

                Map<String, Double> posMap = new HashMap<>();

                posMap.put("x", teammate.getLocation().getX());
                posMap.put("y", teammate.getLocation().getY() + 3.0D);
                posMap.put("z", teammate.getLocation().getZ());

                playersMap.put(teammate.getUniqueId(), posMap);
            }
        });
        return playersMap;
    }
}

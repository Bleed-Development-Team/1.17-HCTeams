package net.frozenorb.foxtrot.listener;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketServerRule;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;

public class ClientListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void joinWaypoint(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        LunarClientAPI.getInstance().sendPacket(event.getPlayer(), new LCPacketServerRule(ServerRule.MINIMAP_STATUS, false));

        new BukkitRunnable(){
            @Override
            public void run(){
                LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint("Spawn", Foxtrot.getInstance().getServerHandler().getSpawnLocation(), Color.GREEN.hashCode(), true, true));

                for (Event events : Foxtrot.getInstance().getEventHandler().getEvents()){
                    if (events.isActive()){
                        if (events instanceof KOTH){
                            Location location = ((KOTH) events).getCapLocation().toLocation(player.getWorld());
                            LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(events.getName() + " KoTH", location, Color.YELLOW.hashCode(), true, true));
                        }
                        if (events.getName().equalsIgnoreCase("Conquest")) {
                            Location location = ((KOTH) events).getCapLocation().toLocation(player.getWorld());
                            LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(events.getName(), location, Color.ORANGE.hashCode(), true, true));
                        }
                        if (events.getName().equalsIgnoreCase("Citadel")) {
                            Location location = ((KOTH) events).getCapLocation().toLocation(player.getWorld());
                            LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(events.getName(), location, Color.MAGENTA.hashCode(), true, true));
                        }
                    }
                }
                if (team != null){
                    if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null){
                        LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(
                                team.getFocusedTeam().getName() + "'s HQ",
                                team.getFocusedTeam().getHQ(),
                                Color.BLUE.hashCode(),
                                true
                        ));
                    }

                    if (team.getTeamRally() != null){
                        LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(
                                "Team Rally",
                                team.getTeamRally(),
                                Color.ORANGE.hashCode(),
                                true
                        ));
                    }

                    if (team.getHQ() != null){
                        LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(
                                "HQ",
                                team.getHQ(),
                                Color.BLUE.hashCode(),
                                true));
                    }
                }
            }
        }.runTaskLater(Foxtrot.getInstance(), 40L);

    }
}

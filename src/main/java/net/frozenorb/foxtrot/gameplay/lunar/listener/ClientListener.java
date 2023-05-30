package net.frozenorb.foxtrot.gameplay.lunar.listener;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketServerRule;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.events.events.EventActivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
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


    public LCWaypoint currentEventWaypoint = null;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void joinWaypoint(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        LunarClientAPI.getInstance().sendPacket(event.getPlayer(), new LCPacketServerRule(ServerRule.MINIMAP_STATUS, false));

        new BukkitRunnable(){
            @Override
            public void run(){
                LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint("Spawn", HCF.getInstance().getServerHandler().getSpawnLocation(), Color.GREEN.getRGB(), true, true));

                if (currentEventWaypoint != null){
                    LunarClientAPI.getInstance().sendWaypoint(player, currentEventWaypoint);
                }

                if (team != null){
                    if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null){
                        LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(
                                team.getFocusedTeam().getName() + "'s HQ",
                                team.getFocusedTeam().getHQ(),
                                Color.BLUE.getRGB(),
                                true
                        ));
                    }

                    if (team.getTeamRally() != null){
                        LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(
                                "Team Rally",
                                team.getTeamRally(),
                                Color.ORANGE.getRGB(),
                                true
                        ));
                    }

                    if (team.getHQ() != null){
                        LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(
                                "HQ",
                                team.getHQ(),
                                Color.BLUE.getRGB(),
                                true));
                    }
                }
            }
        }.runTaskLater(HCF.getInstance(), 20L);
    }

    @EventHandler
    public void eventActivate(EventActivatedEvent event){
        Location location = HCF.getInstance().getTeamHandler().getTeam(event.getEvent().getName()).getHQ();

        if (event.getEvent().getName().equalsIgnoreCase("Citadel")){
            this.currentEventWaypoint = new LCWaypoint("Citadel", location, Color.PINK.getRGB(), true, true);
        } else if (event.getEvent() instanceof KOTH) {
            this.currentEventWaypoint = new LCWaypoint(event.getEvent().getName() + " KOTH", location, Color.BLUE.getRGB(), true, true);
        }
    }
}

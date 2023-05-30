package net.frozenorb.foxtrot.gameplay.events.citadel.listeners;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.gameplay.events.citadel.events.CitadelActivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.citadel.events.CitadelCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventActivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.HourEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.SimpleDateFormat;

public class CitadelListener implements Listener {

    @EventHandler
    public void onKOTHActivated(EventActivatedEvent event) {
        if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
            HCF.getInstance().getServer().getPluginManager().callEvent(new CitadelActivatedEvent());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onKOTHCaptured(EventCapturedEvent event) {
        if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
            Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(event.getPlayer());

            if (playerTeam != null) {
                HCF.getInstance().getCitadelHandler().addCapper(playerTeam.getUniqueId());
                playerTeam.setCitadelsCapped(playerTeam.getCitadelsCapped() + 1);
            }
        }
    }

    @EventHandler
    public void onCitadelActivated(CitadelActivatedEvent event) {
        HCF.getInstance().getCitadelHandler().resetCappers();
    }

    @EventHandler
    public void onCitadelCaptured(CitadelCapturedEvent event) {
        HCF.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.RED + "Citadel" + ChatColor.YELLOW + " is " + ChatColor.DARK_RED + "closed " + ChatColor.YELLOW + "until " + ChatColor.WHITE + (new SimpleDateFormat()).format(HCF.getInstance().getCitadelHandler().getLootable()) + ChatColor.YELLOW + ".");
    }

    @EventHandler(priority=EventPriority.MONITOR) // The monitor is here so we get called 'after' most join events.
    public void onPlayerJoin(PlayerJoinEvent event) {
        Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (playerTeam != null && HCF.getInstance().getCitadelHandler().getCappers().contains(playerTeam.getUniqueId())) {
            event.getPlayer().sendMessage(CitadelHandler.PREFIX + " " + ChatColor.DARK_GREEN + "Your team currently controls Citadel.");
        }
    }


    @EventHandler
    public void onHour(HourEvent event) {
        // Every other hour
        if (event.getHour() % 2 == 0) {
            int respawned = HCF.getInstance().getCitadelHandler().respawnCitadelChests();

            if (respawned != 0) {
                HCF.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.GREEN + "Citadel loot chests have respawned!");
            }
        }
    }

}
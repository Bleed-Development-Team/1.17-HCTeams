package net.frozenorb.foxtrot.team.upgrades;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class UpgradeListener implements Listener {

    /*
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Team team = LandBoard.getInstance().getTeam(player.getLocation());

        if (team == null) return;

        if (team.isMember(player.getUniqueId()) && !team.isRaidable()) {
            HCF.getInstance().getUpgradeHandler().giveEffects(team, player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Team team = LandBoard.getInstance().getTeam(player.getLocation());

        if (team != null && team.isMember(player.getUniqueId())) {
            HCF.getInstance().getUpgradeHandler().removeEffects(team, player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Location to = event.getTo();
        Location from = event.getFrom();

        Player player = event.getPlayer();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }

        Team teamTo = LandBoard.getInstance().getTeam(to);
        Team teamFrom = LandBoard.getInstance().getTeam(from);

        /*
        if (teamFrom != teamTo) {
            if (teamFrom.isMember(player.getUniqueId()) && !teamFrom.getFactionUpgrades().isEmpty()) {
                HCF.getInstance().getUpgradeHandler().removeEffects(teamFrom, player);
            } else if (teamTo.isMember(player.getUniqueId()) && !teamTo.getFactionUpgrades().isEmpty() && !teamTo.isRaidable()) {
                HCF.getInstance().getUpgradeHandler().giveEffects(teamTo, player);
            } else if (!teamTo.isMember(player.getUniqueId()) && teamFrom.isMember(player.getUniqueId()) && !teamFrom.getFactionUpgrades().isEmpty()) {
                HCF.getInstance().getUpgradeHandler().removeEffects(teamFrom, player);
            }
        }

         */
    /*

        if (teamFrom != teamTo){
            if (teamFrom == null && teamTo.isMember(player.getUniqueId())){
                HCF.getInstance().getUpgradeHandler().giveEffects(teamTo, player);
            } else if (teamFrom != null && teamFrom.isMember(player.getUniqueId())){
                HCF.getInstance().getUpgradeHandler().removeEffects(teamFrom, player);
            } else if (teamFrom != null && teamTo.isMember(player.getUniqueId())){
                HCF.getInstance().getUpgradeHandler().giveEffects(teamTo, player);
            }
        }
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent event){
        onPlayerMove(event);
    }

     */
}

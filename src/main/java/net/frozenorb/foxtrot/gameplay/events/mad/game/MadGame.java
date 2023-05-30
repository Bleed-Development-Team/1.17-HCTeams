package net.frozenorb.foxtrot.gameplay.events.mad.game;

import lombok.Getter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.koth.events.EventControlTickEvent;
import net.frozenorb.foxtrot.gameplay.events.koth.events.KOTHControlLostEvent;
import net.frozenorb.foxtrot.gameplay.events.mad.MadHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MadGame implements Listener {

    @Getter private LinkedHashMap<ObjectId, Integer> teamPoints = new LinkedHashMap<>();

    public MadGame() {
        HCF.getInstance().getServer().getPluginManager().registerEvents(this, HCF.getInstance());

        for (Event event : HCF.getInstance().getEventHandler().getEvents()) {
            if (event.getType() != EventType.KOTH) continue;
            KOTH koth = (KOTH) event;
            if (koth.getName().startsWith(MadHandler.KOTH_NAME_PREFIX)) {
                if (!koth.isHidden()) {
                    koth.setHidden(true);
                }

                if (koth.getCapTime() != MadHandler.TIME_TO_CAP) {
                    koth.setCapTime(MadHandler.TIME_TO_CAP);
                }

                koth.activate();
            }
        }

        HCF.getInstance().getServer().broadcastMessage(MadHandler.PREFIX + " " + ChatColor.WHITE + "Mad has started! Use /mad    for more information.");
        HCF.getInstance().getMadHandler().setGame(this);
    }

    public void endGame(final Team winner) {
        if (winner == null) {
            HCF.getInstance().getServer().broadcastMessage(MadHandler.PREFIX + " " + ChatColor.WHITE + "Mad has ended.");
        } else {
            HCF.getInstance().getServer().broadcastMessage(MadHandler.PREFIX + " " + ChatColor.RED + ChatColor.BOLD + winner.getName() + ChatColor.WHITE + " has won Mad!");
            HCF.getInstance().getServer().broadcastMessage(MadHandler.PREFIX + " " + CC.translate("&fThey have been rewarded with &c&l100 &fpoints!"));

            winner.setPoints(winner.getPoints() + 100);
        }

        for (Event koth : HCF.getInstance().getEventHandler().getEvents()) {
            if (koth.getName().startsWith(MadHandler.KOTH_NAME_PREFIX)) {
                koth.deactivate();
            }
        }

        HandlerList.unregisterAll(this);
        HCF.getInstance().getMadHandler().setGame(null);
    }

    @EventHandler
    public void onKOTHCaptured(final EventCapturedEvent event) {
        if (!event.getEvent().getName().startsWith(MadHandler.KOTH_NAME_PREFIX)) {
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (team == null) {
            return;
        }

        if (teamPoints.containsKey(team.getUniqueId())) {
            teamPoints.put(team.getUniqueId(), teamPoints.get(team.getUniqueId()) + 1);
        } else {
            teamPoints.put(team.getUniqueId(), 1);
        }

        teamPoints = sortByValues(teamPoints);
        HCF.getInstance().getServer().broadcastMessage(MadHandler.PREFIX + " " + ChatColor.RED + ChatColor.BOLD + team.getName() + ChatColor.WHITE + " captured " + ChatColor.RED + "Mad" + ChatColor.WHITE + " and earned a point!" + ChatColor.GRAY + " (" + teamPoints.get(team.getUniqueId()) +
                "/" + MadHandler.getCapsToWin() + ")");

        for (Event koth : HCF.getInstance().getEventHandler().getEvents()) {
            if (koth.getName().startsWith(MadHandler.KOTH_NAME_PREFIX)) {
                koth.activate();
            }
        }

        if (teamPoints.get(team.getUniqueId()) >= MadHandler.getCapsToWin()) {
            endGame(team);
            ItemStack conquestKey = InventoryUtils.generateKOTHRewardKey("Mad", 3);
            conquestKey.setAmount(5);

            event.getPlayer().getInventory().addItem(conquestKey);
            if (!event.getPlayer().getInventory().contains(conquestKey)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), conquestKey);
            }
        }
    }

    @EventHandler
    public void onKOTHControlLost(KOTHControlLostEvent event) {
        if (!event.getKOTH().getName().startsWith(MadHandler.KOTH_NAME_PREFIX)) {
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(event.getKOTH().getCurrentCapper()));

        if (team == null) {
            return;
        }

        team.sendMessage(MadHandler.PREFIX + ChatColor.RED + ChatColor.BOLD + " " + event.getKOTH().getCurrentCapper() + ChatColor.WHITE + " was knocked off of " + ChatColor.RED + ChatColor.BOLD + "Mad" + ChatColor.WHITE + "!");
    }
    @EventHandler
    public void onKOTHControlTick(EventControlTickEvent event) {
        
        if (!event.getKOTH().getName().startsWith(MadHandler.KOTH_NAME_PREFIX) || event.getKOTH().getRemainingCapTime() % 5 != 0) {
            return;
        }

        Player capper = HCF.getInstance().getServer().getPlayerExact(event.getKOTH().getCurrentCapper());

        if (capper != null) {
            capper.sendMessage(MadHandler.PREFIX + " " + ChatColor.WHITE + "Attempting to capture " + ChatColor.RED + ChatColor.BOLD + "Mad" + ChatColor.GRAY + " (" + event.getKOTH().getRemainingCapTime() + "s)");
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Team team = HCF.getInstance().getTeamHandler().getTeam(event.getEntity());

        if (team == null || !teamPoints.containsKey(team.getUniqueId())) {
            return;
        }

        teamPoints.put(team.getUniqueId(), Math.max(0, teamPoints.get(team.getUniqueId()) - MadHandler.POINTS_DEATH_PENALTY));
        teamPoints = sortByValues(teamPoints);
        team.sendMessage(MadHandler.PREFIX + ChatColor.WHITE + " Your team has lost " + MadHandler.POINTS_DEATH_PENALTY + " points because of " + event.getEntity().getName() + "'s death!" + ChatColor.GRAY + " (" + teamPoints.get(team.getUniqueId()) + "/" + MadHandler.getCapsToWin() + ")");
    }

    private static LinkedHashMap<ObjectId, Integer> sortByValues(Map<ObjectId, Integer> map) {
        LinkedList<Map.Entry<ObjectId, Integer>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<ObjectId, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<ObjectId, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }

}
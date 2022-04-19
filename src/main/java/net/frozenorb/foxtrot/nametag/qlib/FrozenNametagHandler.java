package net.frozenorb.foxtrot.nametag.qlib;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.nametag.packet.ScoreboardTeamPacketMod;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class FrozenNametagHandler {
    /*
    private static Map<String, Map<String, NametagInfo>> teamMap = new ConcurrentHashMap<>();

    protected static Map<String, Map<String, NametagInfo>> getTeamMap() {
        return teamMap;
    }

    private static List<NametagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());

    private static int teamCreateIndex = 1;

    private static List<NametagProvider> providers = new ArrayList<>();

    private static boolean nametagRestrictionEnabled = false;

    public static boolean isNametagRestrictionEnabled() {
        return nametagRestrictionEnabled;
    }

    public static void setNametagRestrictionEnabled(boolean nametagRestrictionEnabled) {
        FrozenNametagHandler.nametagRestrictionEnabled = nametagRestrictionEnabled;
    }

    private static String nametagRestrictBypass = "";

    public static String getNametagRestrictBypass() {
        return nametagRestrictBypass;
    }

    public static void setNametagRestrictBypass(String nametagRestrictBypass) {
        FrozenNametagHandler.nametagRestrictBypass = nametagRestrictBypass;
    }

    private static boolean initiated = false;

    public static boolean isInitiated() {
        return initiated;
    }

    private static boolean async = true;

    public static boolean isAsync() {
        return async;
    }

    public static void setAsync(boolean async) {
        FrozenNametagHandler.async = async;
    }

    private static int updateInterval = 2;

    public static int getUpdateInterval() {
        return updateInterval;
    }

    public static void setUpdateInterval(int updateInterval) {
        FrozenNametagHandler.updateInterval = updateInterval;
    }

    public static void init() {
        Bukkit.getConsoleSender().sendMessage("[Foxtrot Nametag] Loading nametag handler...");

        Preconditions.checkState(!initiated);
        initiated = true;
        nametagRestrictionEnabled = false;
        nametagRestrictBypass = "&a";
        (new NametagThread()).start();
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new NametagListener(), Foxtrot.getInstance());
        registerProvider(new NametagProvider.DefaultNametagProvider());

        Bukkit.getConsoleSender().sendMessage("[Foxtrot Nametag] Initialized Nametag Handler.");
    }

    public static void registerProvider(NametagProvider newProvider) {
        providers.add(newProvider);
        providers.sort((a, b) -> Ints.compare(b.getWeight(), a.getWeight()));
    }

    public static void reloadPlayer(Player toRefresh) {
        NametagUpdate update = new NametagUpdate(toRefresh);
        if (async) {
            NametagThread.getPendingUpdates().put(update, Boolean.TRUE);
        } else {
            applyUpdate(update);
        }
    }

    public static void reloadOthersFor(Player refreshFor) {
        for (Player toRefresh : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (refreshFor == toRefresh)
                continue;
            reloadPlayer(toRefresh, refreshFor);
        }
    }

    public static void reloadPlayer(Player toRefresh, Player refreshFor) {
        NametagUpdate update = new NametagUpdate(toRefresh, refreshFor);
        if (async) {
            NametagThread.getPendingUpdates().put(update, Boolean.TRUE);
        } else {
            applyUpdate(update);
        }
    }

    protected static void applyUpdate(NametagUpdate nametagUpdate) {
        Player toRefreshPlayer = Foxtrot.getInstance().getServer().getPlayerExact(nametagUpdate.getToRefresh());
        if (toRefreshPlayer == null)
            return;
        if (nametagUpdate.getRefreshFor() == null) {
            for (Player refreshFor : Foxtrot.getInstance().getServer().getOnlinePlayers())
                reloadPlayerInternal(toRefreshPlayer, refreshFor);
        } else {
            Player refreshForPlayer = Foxtrot.getInstance().getServer().getPlayerExact(nametagUpdate.getRefreshFor());
            if (refreshForPlayer != null)
                reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
        }
    }

    protected static void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if (!refreshFor.hasMetadata("qLibNametag-LoggedIn"))
            return;
        NametagInfo provided = null;
        int providerIndex = 0;
        while (provided == null)
            provided = providers.get(providerIndex++).fetchNametag(toRefresh, refreshFor);

        Map<String, NametagInfo> teamInfoMap = new HashMap<>();
        if (teamMap.containsKey(refreshFor.getName()))
            teamInfoMap = teamMap.get(refreshFor.getName());
        (new ScoreboardTeamPacketMod(provided.getName(), Collections.singletonList(toRefresh.getName()), 3)).sendToPlayer(refreshFor);
        teamInfoMap.put(toRefresh.getName(), provided);
        teamMap.put(refreshFor.getName(), teamInfoMap);
    }

    protected static void initiatePlayer(Player player) {
        for (NametagInfo teamInfo : registeredTeams)
            teamInfo.getTeamAddPacket().sendToPlayer(player);
    }

    protected static NametagInfo getOrCreate(String prefix, String suffix) {
        for (NametagInfo teamInfo : registeredTeams) {
            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix))
                return teamInfo;
        }
        NametagInfo newTeam = new NametagInfo(String.valueOf(teamCreateIndex++), prefix, suffix);
        registeredTeams.add(newTeam);
        ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();
        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers())
            addPacket.sendToPlayer(player);
        return newTeam;
    }

     */
}

package net.frozenorb.foxtrot.walls;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.LCPacket;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketWorldBorderCreateNew;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketWorldBorderRemove;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.commands.op.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.Coordinate;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class WallsHandler extends Thread {

    public static final int REGION_DISTANCE = 8;
    public static final int REGION_DISTANCE_SQUARED = REGION_DISTANCE * REGION_DISTANCE;

    private static final Map<String, Map<Location, Long>> sentBlockChanges = new HashMap<>();
    // The pair represents the 2 corners of the claim
    private static final Map<String, Map<String, Long>> sentLunarWalls = new HashMap<>();

    //private static final Map<String, Map<Pair<Pair<Double, Double>, Pair<Double, Double>>, Long>> sentLunarWalls = new HashMap<>();

    public WallsHandler() {
        super("Foxtrot - Walls Thread");
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                try {
                    checkPlayer(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(250L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPlayer(Player player) {
        try {
            List<Claim> claims = new LinkedList<>();

            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            
            boolean tagged = SpawnTagHandler.isTagged(player);
            boolean hasPvPTimer = HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId());
            
            if (!tagged && !hasPvPTimer) {
                clearPlayer(player);
                return;
            }

            for (Map.Entry<Claim, Team> regionDataEntry : LandBoard.getInstance().getRegionData(player.getLocation(), REGION_DISTANCE, REGION_DISTANCE, REGION_DISTANCE)) {
                Claim claim = regionDataEntry.getKey();
                Team team = regionDataEntry.getValue();

                // Ignore claims if the player is in them.
                // There might become a time where we need to remove this
                // and make it a per-claim-type check, however for now
                // all checks work fine with this here.
                if (claim.contains(player)) {
                    continue;
                }

                if (team.getOwner() == null) {
                    if (team.hasDTRBitmask(DTRBitmask.SAFE_ZONE) && tagged) {
                        // If the team is a SAFE_ZONE (IE spawn), they're not inside of it, and they're spawn tagged
                        claims.add(claim);
                    } else if ((team.hasDTRBitmask(DTRBitmask.KOTH) || team.hasDTRBitmask(DTRBitmask.CITADEL)) && hasPvPTimer) {
                        // If it's an event zone (KOTH or Citadel) and they have a PvP Timer
                        claims.add(claim);
                    }
                } else {
                    if (HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        // If it's an actual claim and the player has a PvP Timer
                        claims.add(claim);
                    } else if (CustomTimerCreateCommand.isSOTWTimer()){
                        if (team.isClaimLocked()){
                            claims.add(claim);
                        }
                    }
                }
            }

            if (claims.size() == 0) {
                clearPlayer(player);
            } else {
                if (!sentBlockChanges.containsKey(player.getName())) {
                    sentBlockChanges.put(player.getName(), new HashMap<>());
                }

                if (LunarClientAPI.getInstance().isRunningLunarClient(player.getUniqueId()) && !sentLunarWalls.containsKey(player.getName())) {
                    sentLunarWalls.put(player.getName(), new HashMap<>());
                }

                // Remove borders after they 'expire' -- This is used to get rid of block changes the player has walked away from,
                // whose value in the map hasn't been updated recently.
                if (!LunarClientAPI.getInstance().isRunningLunarClient(player.getUniqueId())) {
                    Iterator<Map.Entry<Location, Long>> bordersIterator = sentBlockChanges.get(player.getName()).entrySet().iterator();

                    while (bordersIterator.hasNext()) {
                        Map.Entry<Location, Long> border = bordersIterator.next();

                        if (System.currentTimeMillis() >= border.getValue()) {
                            Location loc = border.getKey();

                            if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                                continue;
                            }

                            Block block = loc.getBlock();
                            player.sendBlockChange(loc, block.getBlockData());
                            bordersIterator.remove();
                        }
                    }
                } else {
                    //
                    for (Map.Entry<String, Long> key : sentLunarWalls.get(player.getName()).entrySet()) {
                        if (key.getValue() < System.currentTimeMillis()) continue;

                        sendLunarPacket(
                                player,
                                new LCPacketWorldBorderRemove(key.getKey())
                        );
                    }
                }
                if (!HCF.getInstance().getServerHandler().isEOTW()) {
                    for (Claim claim : claims) {
                        sendClaimToPlayer(player, claim);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendClaimToPlayer(Player player, Claim claim) {
        // This gets us all the coordinates on the outside of the claim.
        // Probably could be made better

        //
        // send claim to player
        if (LunarClientAPI.getInstance().isRunningLunarClient(player.getUniqueId())) {
            String id = UUID.randomUUID().toString();
            sendLunarPacket(player,
                    new LCPacketWorldBorderCreateNew(
                        id,
                        player.getWorld().getUID().toString(),
                            true,
                            false,
                            false,
                            0xFF0000,
                            claim.getX1() * 1.00,
                            claim.getZ1() * 1.00,
                            claim.getX2() * 1.00,
                            claim.getZ2() * 1.00
                    )
            );

            Bukkit.broadcastMessage("Sent lunar pcket for " + player.getName() + " " + claim.getX1() + " " + claim.getX2());

            sentLunarWalls.get(player.getName()).put(id, System.currentTimeMillis() + 4000L);

            return;
        }

        for (Coordinate coordinate : claim) {
            Location onPlayerY = new Location(player.getWorld(), coordinate.getX(), player.getLocation().getY(), coordinate.getZ());

            // Ignore an entire pillar if the block closest to the player is further than the max distance (none of the others will be close enough, either)
            if (onPlayerY.distanceSquared(player.getLocation()) > REGION_DISTANCE_SQUARED) {
                continue;
            }

            for (int i = -4; i < 5; i++) {
                Location check = onPlayerY.clone().add(0, i, 0);

                if (Objects.requireNonNull(check.getWorld()).isChunkLoaded(check.getBlockX() >> 4, check.getBlockZ() >> 4) && check.getBlock().getType().isTransparent() && check.distanceSquared(onPlayerY) < REGION_DISTANCE_SQUARED) {
                    player.sendBlockChange(check, Material.RED_STAINED_GLASS.createBlockData()); // Red stained glass
                    sentBlockChanges.get(player.getName()).put(check, System.currentTimeMillis() + 4000L); // The time the glass will stay for if the player walks away
                }
            }
        }
    }

    private static void clearPlayer(Player player) {

        if (LunarClientAPI.getInstance().isRunningLunarClient(player.getUniqueId()) && sentLunarWalls.containsKey(player.getName())) {
            for (String key : sentLunarWalls.get(player.getName()).keySet()) {
                sendLunarPacket(
                        player,
                        new LCPacketWorldBorderRemove(key)
                );
            }

            sentLunarWalls.remove(player.getName());
            return;
        }

        if (sentBlockChanges.containsKey(player.getName())) {
            for (Location changedLoc : sentBlockChanges.get(player.getName()).keySet()) {
                if (!changedLoc.getWorld().isChunkLoaded(changedLoc.getBlockX() >> 4, changedLoc.getBlockZ() >> 4)) {
                    continue;
                }

                Block block = changedLoc.getBlock();
                player.sendBlockChange(changedLoc, block.getBlockData());
            }

            sentBlockChanges.remove(player.getName());
        }
    }

    public static void sendLunarPacket(Player player, LCPacket packet) {
        Bukkit.getScheduler().runTask(HCF.getInstance(), (runnable) -> {
            LunarClientAPI.getInstance().sendPacket(player, packet);
        });
    }

}
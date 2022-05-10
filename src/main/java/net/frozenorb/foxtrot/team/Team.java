package net.frozenorb.foxtrot.team;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.events.region.cavern.CavernHandler;
import net.frozenorb.foxtrot.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.map.stats.StatsHandler;
import net.frozenorb.foxtrot.persist.maps.DeathbanMap;
import net.frozenorb.foxtrot.redis.RedisCommand;
import net.frozenorb.foxtrot.serialization.LocationSerializer;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.CuboidRegion;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.frozenorb.foxtrot.uuid.FrozenUUIDCache;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.text.DecimalFormat;
import java.util.*;

public class Team {

    // Constants //
    public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");
    public static final String GRAY_LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 36);
    public static final ChatColor ALLY_COLOR = ChatColor.BLUE;
    public static final int MAX_CLAIMS = 2;
    public static final int MAX_FORCE_INVITES = 5;

    // Internal //
    @Getter private boolean needsSave = false;
    @Getter private boolean loading = false;

    // Persisted //
    @Getter @Setter private ObjectId uniqueId;
    @Getter private String name;
    @Getter private Location HQ;
    @Getter private double balance;
    @Getter private double DTR;
    @Getter private long DTRCooldown;
    @Getter private List<Claim> claims = new ArrayList<>();
    @Getter private List<Subclaim> subclaims = new ArrayList<>();
    @Getter private UUID owner = null;
    @Getter private Set<UUID> members = new HashSet<>();
    @Getter private Set<UUID> captains = new HashSet<>();
    @Getter private Set<UUID> coleaders = new HashSet<>();
    @Getter private Set<UUID> invitations = new HashSet<>();
    @Getter private Set<ObjectId> allies = new HashSet<>();
    @Getter private Set<ObjectId> requestedAllies = new HashSet<>();
    @Getter private String announcement;
    @Getter private int maxOnline = -1;
    @Getter private boolean powerFaction = false;
    @Getter private int lives = 0;
    @Getter private int points = 0;
    @Getter private int kills = 0;
    @Getter private int kothCaptures = 0;
    @Getter private int diamondsMined = 0;
    @Getter private int deaths = 0;
    @Getter private int citadelsCapped = 0;
    @Getter private int killstreakPoints = 0;
    @Getter private int playtimePoints = 0;
    @Getter private int spawnersInClaim = 0;
    @Getter private int spentPoints = 0; // points spent on faction upgrades (kinda aids)

	@Getter private Map<String, Integer> upgradeToTier = new HashMap<>();

    @Getter private int forceInvites = MAX_FORCE_INVITES;
    @Getter private Set<UUID> historicalMembers = new HashSet<>(); // this will store all players that were once members

    // Not persisted //
    @Getter @Setter private UUID focused;
    @Getter @Setter private Team focusedTeam;
    @Getter @Setter private Location teamRally;
    @Getter @Setter private long lastRequestReport;
    
    @Getter @Setter private int bards;
    @Getter @Setter private int archers;
    @Getter @Setter private int rogues;

    public Team(String name) {
        this.name = name;
    }

    public void setDTR(double newDTR) {
        setDTR(newDTR, null);
    }

    public void setDTR(double newDTR, Player actor) {
        if (DTR == newDTR) {
            return;
        }

        if (DTR <= 0 && newDTR > 0) {
            TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NO_LONGER_RAIDABLE, ImmutableMap.of());
        }

        if (0 < DTR && newDTR <= 0) {
            TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, actor == null ? ImmutableMap.of() : ImmutableMap.of("actor", actor.getName()));
        }

        if (!isLoading()) {
            if (actor != null) {
                Foxtrot.getInstance().getLogger().info("[DTR Change] " + getName() + ": " + DTR + " --> " + newDTR + ". Actor: " + actor.getName());
            } else {
                Foxtrot.getInstance().getLogger().info("[DTR Change] " + getName() + ": " + DTR + " --> " + newDTR);
            }
        }

        this.DTR = newDTR;
        flagForSave();
    }

    public void setName(String name) {
        this.name = name;
        flagForSave();
    }

    public String getName(Player player) {
        if (name.equals(GlowHandler.getGlowTeamName()) && this.getMembers().size() == 0) {
            return ChatColor.GOLD + "Glowstone Mountain"; // override team name
        } else if (name.equals(CavernHandler.getCavernTeamName()) && this.getMembers().size() == 0) {
            return ChatColor.AQUA + "Cavern";
        } else if (owner == null) {
            if (hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                switch (player.getWorld().getEnvironment()) {
                    case NETHER:
                        return (ChatColor.GREEN + "Nether Spawn");
                    case THE_END:
                        return (ChatColor.GREEN + "The End Safezone");
                }

                return (ChatColor.GREEN + "Spawn");
            } else if (hasDTRBitmask(DTRBitmask.KOTH)) {
                return (ChatColor.AQUA + getName() + ChatColor.GOLD + " KOTH");
            } else if (hasDTRBitmask(DTRBitmask.CITADEL)) {
                return (ChatColor.DARK_PURPLE + "Citadel");
            } else if (hasDTRBitmask(DTRBitmask.ROAD)) {
                return (ChatColor.GOLD + getName().replace("Road", " Road"));
            } else if (hasDTRBitmask(DTRBitmask.CONQUEST)) {
                return (ChatColor.YELLOW + "Conquest");
            }
        }

        if (isMember(player.getUniqueId())) {
            return (ChatColor.GREEN + getName());
        } else if (isAlly(player.getUniqueId())) {
            return (Team.ALLY_COLOR + getName());
        } else {
            return (ChatColor.RED + getName());
        }
    }

    public String getFormattedDTR(){
        return getDTRColor() + DTR_FORMAT.format(getDTR()) + getDTRSuffix();
    }

    public void addMember(UUID member) {
        if (members.add(member)) {
            historicalMembers.add(member);

            if (this.loading) return;
            TeamActionTracker.logActionAsync(this, TeamActionType.PLAYER_JOINED, ImmutableMap.of(
                    "playerId", member
            ));

            flagForSave();
        }
    }

    public void addCaptain(UUID captain) {
        if (captains.add(captain) && !this.isLoading()) {
            TeamActionTracker.logActionAsync(this, TeamActionType.PROMOTED_TO_CAPTAIN, ImmutableMap.of(
                    "playerId", captain
            ));

            flagForSave();
        }
    }

    public void addCoLeader(UUID co) {
        if (coleaders.add(co) && !this.isLoading()) {
            TeamActionTracker.logActionAsync(this, TeamActionType.PROMOTED_TO_CO_LEADER, ImmutableMap.of(
                    "playerId", co
            ));

            flagForSave();
        }
    }

    public void setBalance(double balance) {
        this.balance = balance;
        flagForSave();
    }

    public void setDTRCooldown(long dtrCooldown) {
        this.DTRCooldown = dtrCooldown;
        flagForSave();
    }

    public void removeCaptain(UUID captain) {
        if (captains.remove(captain)) {
            TeamActionTracker.logActionAsync(this, TeamActionType.DEMOTED_FROM_CAPTAIN, ImmutableMap.of(
                    "playerId", captain
            ));

            flagForSave();
        }
    }

    public void removeCoLeader(UUID co) {
        if (coleaders.remove(co)) {
            TeamActionTracker.logActionAsync(this, TeamActionType.DEMOTED_FROM_CO_LEADER, ImmutableMap.of(
                    "playerId", co
            ));

            flagForSave();
        }
    }

    public void setOwner(UUID owner) {
        this.owner = owner;

        if (owner != null) {
            members.add(owner);
            coleaders.remove(owner);
            captains.remove(owner);
        }

        if (this.loading) return;
        TeamActionTracker.logActionAsync(this, TeamActionType.LEADER_CHANGED, ImmutableMap.of(
                "playerId", owner
        ));

        flagForSave();
    }

    public void setMaxOnline(int maxOnline) {
        this.maxOnline = maxOnline;
        flagForSave();
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;

        if (this.loading) return;
        TeamActionTracker.logActionAsync(this, TeamActionType.ANNOUNCEMENT_CHANGED, ImmutableMap.of(
                "newAnnouncement", announcement
        ));

        flagForSave();
    }

    public void setHQ(Location hq) {
        String oldHQ = this.HQ == null ? "None" : (getHQ().getBlockX() + ", " + getHQ().getBlockY() + ", " + getHQ().getBlockZ());
        String newHQ = hq == null ? "None" : (hq.getBlockX() + ", " + hq.getBlockY() + ", " + hq.getBlockZ());
        this.HQ = hq;

        if (this.loading) return;
        TeamActionTracker.logActionAsync(this, TeamActionType.HEADQUARTERS_CHANGED, ImmutableMap.of(
                "oldHq", oldHQ,
                "newHq", newHQ
        ));

        flagForSave();
    }

    public void setPowerFaction( boolean bool ) {
        this.powerFaction = bool;
        if( bool ) {
            TeamHandler.addPowerFaction(this);
        } else {
            TeamHandler.removePowerFaction(this);
        }

        if (this.loading) return;
        TeamActionTracker.logActionAsync(this, TeamActionType.POWER_FAC_STATUS_CHANGED, ImmutableMap.of(
                "powerFaction", bool
        ));

        flagForSave();
    }

    public void setLives( int lives ) {
        this.lives = lives;
        flagForSave();
    }

    public boolean addLives( int lives ) {
        if( lives < 0 ) {
            return false;
        }
        this.lives += lives;
        flagForSave();
        return true;
    }

    public boolean removeLives( int lives ) {
        if( this.lives < lives || lives < 0) {
            return false; //You twat.
        }
        this.lives -= lives;
        flagForSave();
        return true;
    }

    public void disband() {
        try {
            if (owner != null) {
                double refund = balance;

                for (Claim claim : claims) {
                    refund += Claim.getPrice(claim, this, false);
                }

                FrozenEconomyHandler.deposit(owner, refund);
                Foxtrot.getInstance().getWrappedBalanceMap().setBalance(owner, FrozenEconomyHandler.getBalance(owner));
                Foxtrot.getInstance().getLogger().info("Economy Logger: Depositing " + refund + " into " + UUIDUtils.name(owner) + "'s account: Disbanded team");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ObjectId allyId : getAllies()) {
            Team ally = Foxtrot.getInstance().getTeamHandler().getTeam(allyId);

            if (ally != null) {
                ally.getAllies().remove(getUniqueId());
            }
        }

        for (UUID uuid : members) {
            Foxtrot.getInstance().getChatModeMap().setChatMode(uuid, ChatMode.PUBLIC);
        }

        Foxtrot.getInstance().getTeamHandler().removeTeam(this);
        LandBoard.getInstance().clear(this);

        new BukkitRunnable() {

            public void run() {
                Foxtrot.getInstance().runRedisCommand(redis -> {
                    redis.del("fox_teams." + name.toLowerCase());
                    return (null);
                });

                DBCollection teamsCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Teams");
                teamsCollection.remove(getJSONIdentifier());
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());

        needsSave = false;
    }

    public void rename(String newName) {
        final String oldName = name;

        Foxtrot.getInstance().getTeamHandler().removeTeam(this);

        this.name = newName;

        Foxtrot.getInstance().getTeamHandler().setupTeam(this);

        Foxtrot.getInstance().runRedisCommand(new RedisCommand<Object>() {

            @Override
            public Object execute(Jedis redis) {
                redis.del("fox_teams." + oldName.toLowerCase());
                return (null);
            }

        });

        // We don't need to do anything here as all we're doing is changing the name, not the Unique ID (which is what Mongo uses)
        // therefore, Mongo will be notified of this once the 'flagForSave()' down below gets processed.

        for (Claim claim : getClaims()) {
            claim.setName(claim.getName().replaceAll(oldName, newName));
        }

        flagForSave();
    }


    public List<Player> getNearbyTeamMembers(Player player) {
        List<Player> valid = new ArrayList<>();
        Team sourceTeam = this;

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity entity : player.getNearbyEntities(20, 20 / 2, 20)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;

                if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(nearbyPlayer.getUniqueId())) {
                    continue;
                }


                boolean isFriendly = sourceTeam.isMember(nearbyPlayer.getUniqueId());

                if (isFriendly) {
                    valid.add(nearbyPlayer);
                }
            }
        }

        valid.add(player);
        return (valid);
    }


    public List<Player> getNearbyTeamMembers(Entity inputEntity) {
        List<Player> valid = new ArrayList<>();
        Team sourceTeam = this;

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity entity : inputEntity.getNearbyEntities(20, 20 / 2, 20)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;

                if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(nearbyPlayer.getUniqueId())) {
                    continue;
                }

                boolean isFriendly = sourceTeam.isMember(nearbyPlayer.getUniqueId());

                if (isFriendly) {
                    valid.add(nearbyPlayer);
                }
            }
        }

        return (valid);
    }


    public void setForceInvites(int forceInvites) {
        this.forceInvites = forceInvites;
        flagForSave();
    }

    public void setPoints(int points) {
        this.points = points;
        flagForSave();
    }

    public void setKills(int kills) {
        this.kills = kills;
        recalculatePoints();
        flagForSave();
    }
    
    public void setDeaths(int deaths) {
        this.deaths = deaths;
        recalculatePoints();
        flagForSave();
    }
    
    public void setKothCaptures(int kothCaptures) {
        this.kothCaptures = kothCaptures;
        recalculatePoints();
        flagForSave();
    }

    public void setDiamondsMined(int diamondsMined) {
        this.diamondsMined = diamondsMined;
        recalculatePoints();
        flagForSave();
    }

    public void setCitadelsCapped(int citadels) {
        this.citadelsCapped = citadels;
        recalculatePoints();
        flagForSave();
    }

    public void setKillstreakPoints(int killstreakPoints) {
        this.killstreakPoints = killstreakPoints;
        recalculatePoints();
        flagForSave();
    }

    public void addKillstreakPoints(int killstreakPoints) {
        this.killstreakPoints += killstreakPoints;
        recalculatePoints();
        flagForSave();
    }

    public void setPlaytimePoints(int playtimePoints) {
        this.playtimePoints = playtimePoints;
        recalculatePoints();
        flagForSave();
    }

    public void addPlaytimePoints(int playtimePoints) {
        this.playtimePoints += playtimePoints;
        recalculatePoints();
        flagForSave();
    }

    public void addSpawnersInClaim(int amount) {
        spawnersInClaim += amount;

        if (spawnersInClaim < 0) {
            spawnersInClaim = 0;
        }

        recalculatePoints();
        flagForSave();
    }

    public void removeSpawnersInClaim(int amount) {
        spawnersInClaim -= amount;

        if (spawnersInClaim < 0) {
            spawnersInClaim = 0;
        }

        recalculatePoints();
        flagForSave();
    }

    public void setSpawnersInClaim(int amount) {
        if (amount < 0) {
            amount = 0;
        }

        spawnersInClaim = amount;
        recalculatePoints();
        flagForSave();
    }

    public void recalculateSpawnersInClaims() {
	    new BukkitRunnable() {
		    @Override
		    public void run() {
			    setSpawnersInClaim(findSpawners().size());
		    }
	    }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public List<CreatureSpawner> findSpawners() {
        if (Bukkit.isPrimaryThread()) {
            throw new RuntimeException("Cannot call Team#findSpawners on main thread");
        }

        List<CreatureSpawner> list = new ArrayList<>();

        // Iterate through chunks' tile entities rather than every block
        for (Claim claim : getClaims()) {
            final World world = Bukkit.getWorld(claim.getWorld());
            final Location minPoint = claim.getMinimumPoint();
            final Location maxPoint = claim.getMaximumPoint();
            final int minChunkX = ((int) minPoint.getX()) >> 4;
            final int minChunkZ = ((int) minPoint.getZ()) >> 4;
            final int maxChunkX = ((int) maxPoint.getX()) >> 4;
            final int maxChunkZ = ((int) maxPoint.getZ()) >> 4;

            for (int chunkX = minChunkX; chunkX < maxChunkX + 1; chunkX++) {
                for (int chunkZ = minChunkZ; chunkZ < maxChunkZ + 1; chunkZ++) {
                    Chunk chunk = world.getChunkAt(chunkX, chunkZ);

                    for (BlockState blockState : chunk.getTileEntities()) {
                        // Check if the block is a mob spawner
                        if (blockState instanceof CreatureSpawner) {
                            // Even though we're iterating through chunks' tile entities
                            // we need to make sure that the block's location is within
                            // the claim (because claims don't have to align with chunks)
                            final Location loc = blockState.getLocation();

                            if (loc.getX() >= minPoint.getX() && loc.getZ() >= minPoint.getZ() &&
                                loc.getX() <= maxPoint.getX() && loc.getZ() <= maxPoint.getZ()) {
                                list.add((CreatureSpawner) blockState);
                            }
                        }
                    }
                }
            }
        }

        return list;
    }

    public void spendPoints(int points) {
        spentPoints += points;
        recalculatePoints();
        flagForSave();
    }

	public void setSpentPoints(int points) {
		spentPoints = points;
		recalculatePoints();
		flagForSave();
	}

    public void recalculatePoints() {
        int basePoints = 0;

        basePoints += (Math.floor(kills / 5.0D)) * 10;
        basePoints -= (Math.floor(deaths / 5.0D)) * 15;
        basePoints += kothCaptures * 25;
        basePoints += (diamondsMined / 500) * 15;
        basePoints += citadelsCapped * 100;
        basePoints += spawnersInClaim * 5;
        basePoints += killstreakPoints;
        basePoints += playtimePoints;
        basePoints -= spentPoints;

        if (basePoints < 0) {
            basePoints = 0;
        }

        this.points = basePoints;
    }

    public String[] getPointBreakDown() {
        int basePoints = 0;

        basePoints += (Math.floor(kills / 5.0D)) * 10;
        basePoints -= (Math.floor(deaths / 5.0D)) * 15;
        basePoints += kothCaptures * 25;
        basePoints += citadelsCapped * 100;
        basePoints += (diamondsMined / 500) * 15;
        basePoints += spawnersInClaim * 5;
        basePoints += killstreakPoints;
        basePoints += playtimePoints;
        basePoints -= spentPoints;

        if (basePoints < 0) {
            basePoints = 0;
        }

        return new String[]{
                "Base Points: " + basePoints,
                "Kills Points: (" + kills + " kills / 5) * 10 = " + ((Math.floor(kills / 5.0D)) * 10),
                "Deaths Points: (" + deaths + " deaths / 5) * 15 = " + ((Math.floor(deaths / 5.0D)) * 15),
                "KOTH Captures Points: (" + kothCaptures + " caps) * 25 = " + (kothCaptures * 5),
                "Citadel Captures Points: (" + citadelsCapped + " caps) * 100 = " + (citadelsCapped * 100),
                "Diamonds Mined Points: (" + diamondsMined + " mined / 500) * 15 = " + ((diamondsMined / 500) * 15),
                "Spawners Points: (" + spawnersInClaim + " spawners) * 5 = " + (spawnersInClaim * 5),
                "Killstreaks Points: " + killstreakPoints,
                "Playtime Points: " + playtimePoints,
                "Spent Points: " + spentPoints
        };
    }

    public void flagForSave() {
        needsSave = true;
    }

    public boolean isOwner(UUID check) {
        return (check.equals(owner));
    }

    public boolean isMember(UUID check) {
        return members.contains(check);
    }

    public boolean isCaptain(UUID check) {
        return captains.contains(check);
    }

    public boolean isCoLeader(UUID check) {
        return coleaders.contains(check);
    }

    public void validateAllies() {
        Iterator<ObjectId> allyIterator = getAllies().iterator();

        while (allyIterator.hasNext()) {
            ObjectId ally = allyIterator.next();
            Team checkTeam = Foxtrot.getInstance().getTeamHandler().getTeam(ally);

            if (checkTeam == null) {
                allyIterator.remove();
            }
        }
    }

    public boolean isAlly(UUID check) {
        Team checkTeam = Foxtrot.getInstance().getTeamHandler().getTeam(check);
        return (checkTeam != null && isAlly(checkTeam));
    }

    public boolean isAlly(Team team) {
        return (getAllies().contains(team.getUniqueId()));
    }

    public boolean ownsLocation(Location location) {
        return (LandBoard.getInstance().getTeam(location) == this);
    }

    public boolean ownsClaim(Claim claim) {
        return (claims.contains(claim));
    }

    public boolean removeMember(UUID member) {
        members.remove(member);
        captains.remove(member);
        coleaders.remove(member);

        // If the owner leaves (somehow)
        if (isOwner(member)) {
            Iterator<UUID> membersIterator = members.iterator();
            this.owner = membersIterator.hasNext() ? membersIterator.next() : null;
        }

        try {
            for (Subclaim subclaim : subclaims) {
                if (subclaim.isMember(member)) {
                    subclaim.removeMember(member);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DTR > getMaxDTR()) {
            DTR = getMaxDTR();
        }

        if (this.loading) return false;
        TeamActionTracker.logActionAsync(this, TeamActionType.MEMBER_REMOVED, ImmutableMap.of(
                "playerId", member
        ));

        flagForSave();
        return (owner == null || members.size() == 0);
    }

    public boolean hasDTRBitmask(DTRBitmask bitmaskType) {
        if (getOwner() != null) {
            return (false);
        }

        int dtrInt = (int) DTR;
        return (((dtrInt & bitmaskType.getBitmask()) == bitmaskType.getBitmask()));
    }

    public int getOnlineMemberAmount() {
        int amt = 0;

        for (UUID member : getMembers()) {
            Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);

            if (exactPlayer != null && !exactPlayer.hasMetadata("invisible")) {
                amt++;
            }
        }

        return (amt);
    }

    public Collection<Player> getOnlineMembers() {
        List<Player> players = new ArrayList<>();

        for (UUID member : getMembers()) {
            Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);

            if (exactPlayer != null && !exactPlayer.hasMetadata("invisible")) {
                players.add(exactPlayer);
            }
        }

        return (players);
    }

    public Collection<UUID> getOfflineMembers() {
        List<UUID> players = new ArrayList<>();

        for (UUID member : getMembers()) {
            Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);

            if (exactPlayer == null || exactPlayer.hasMetadata("invisible")) {
                players.add(member);
            }
        }

        return (players);
    }

    public Subclaim getSubclaim(String name) {
        for (Subclaim subclaim : subclaims) {
            if (subclaim.getName().equalsIgnoreCase(name)) {
                return (subclaim);
            }
        }

        return (null);
    }

    public Subclaim getSubclaim(Location location) {
        for (Subclaim subclaim : subclaims) {
            if (new CuboidRegion(subclaim.getName(), subclaim.getLoc1(), subclaim.getLoc2()).contains(location)) {
                return (subclaim);
            }
        }

        return (null);
    }

    public int getSize() {
        return (getMembers().size());
    }

    public boolean isRaidable() {
        return (DTR <= 0);
    }

    public void playerDeath(String playerName, double dtrLoss, Player killer) {
        double oldDTR = getDTR();
        double newDTR = Math.max(DTR - dtrLoss, -.99);

        TeamActionTracker.logActionAsync(this, TeamActionType.MEMBER_DEATH, ImmutableMap.of(
                "playerName", playerName,
                "dtrLoss", dtrLoss,
                "oldDtr", DTR,
                "newDtr", newDTR
        ));

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Member Death: " + ChatColor.WHITE + playerName);
                player.sendMessage(ChatColor.RED + "DTR: " + ChatColor.WHITE + DTR_FORMAT.format(newDTR));
            }
        }

        if (newDTR < 0.0 && oldDTR > 0.0) {
            if (killer != null){
                Foxtrot.getInstance().getRaidableTeamsMap().add(killer.getUniqueId(), 1);
            }
        }

        Foxtrot.getInstance().getLogger().info("[TeamDeath] " + name + " > " + "Player death: [" + playerName + "]");
        setDTR(newDTR);



        if (isRaidable()) {
            TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of());
            DTRCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeRaidable();
        } else {
            DTRCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeDeath();
        }

        DTRHandler.markOnDTRCooldown(this);
    }

    public double getDTRIncrement() {
        return (getDTRIncrement(getOnlineMemberAmount()));
    }

    public double getDTRIncrement(int playersOnline) {
        double dtrPerHour = DTRHandler.getBaseDTRIncrement(getSize()) * playersOnline;
        return (dtrPerHour / 60);
    }

    public double getMaxDTR() {
        return (DTRHandler.getMaxDTR(getSize()));
    }

    public void load(BasicDBObject obj) {
        loading = true;
        setUniqueId(obj.getObjectId("_id"));
        setOwner(obj.getString("Owner") == null ? null : UUID.fromString(obj.getString("Owner")));
        if (obj.containsKey("CoLeaders")) for (Object coLeader : (BasicDBList) obj.get("CoLeaders")) addCoLeader(UUID.fromString((String) coLeader));
        if (obj.containsKey("Captains")) for (Object captain : (BasicDBList) obj.get("Captains")) addCaptain(UUID.fromString((String) captain));
        if (obj.containsKey("Members")) for (Object member : (BasicDBList) obj.get("Members")) addMember(UUID.fromString((String) member));
        if (obj.containsKey("Invitations")) for (Object invite : (BasicDBList) obj.get("Invitations")) getInvitations().add(UUID.fromString((String) invite));
        if (obj.containsKey("DTR")) setDTR(obj.getDouble("DTR"));
        if (obj.containsKey("DTRCooldown")) setDTRCooldown(obj.getDate("DTRCooldown").getTime());
        if (obj.containsKey("Balance")) setBalance(obj.getDouble("Balance"));
        if (obj.containsKey("MaxOnline")) setMaxOnline(obj.getInt("MaxOnline"));
        if (obj.containsKey("HQ")) setHQ(LocationSerializer.deserialize((BasicDBObject) obj.get("HQ")));
        if (obj.containsKey("Announcement")) setAnnouncement(obj.getString("Announcement"));
        if (obj.containsKey("PowerFaction")) setPowerFaction(obj.getBoolean("PowerFaction"));
        if (obj.containsKey("Lives")) setLives(obj.getInt("Lives"));
        if (obj.containsKey("Claims")) for (Object claim : (BasicDBList) obj.get("Claims")) getClaims().add(Claim.fromJson((BasicDBObject) claim));
        if (obj.containsKey("Subclaims")) for (Object subclaim : (BasicDBList) obj.get("Subclaims")) getSubclaims().add(Subclaim.fromJson((BasicDBObject) subclaim));
        if (obj.containsKey("PlaytimePoints")) setPlaytimePoints(obj.getInt("PlaytimePoints"));
        if (obj.containsKey("SpentPoints")) setSpentPoints(obj.getInt("SpentPoints"));
        if (obj.containsKey("SpawnersInClaim")) setSpawnersInClaim(obj.getInt("SpawnersInClaim"));

        // Load team upgrades if they exist
        if (obj.containsKey("Upgrades")) for (Object upgrade : (BasicDBList) obj.get("Upgrades")) upgradeToTier.put(((BasicDBObject) upgrade).getString("UpgradeName"), ((BasicDBObject) upgrade).getInt("Tier"));

        loading = false;
    }

    public void load(String str) {
        load(str, false);
    }

    public void load(String str, boolean forceSave) {
        loading = true;
        String[] lines = str.split("\n");

        for (String line : lines) {
            if (line.indexOf(':') == -1) {
                System.out.println("Found an invalid line... `" + line + "`");
                continue;
            }

            String identifier = line.substring(0, line.indexOf(':'));
            String[] lineParts = line.substring(line.indexOf(':') + 1).split(",");

            if (identifier.equalsIgnoreCase("Owner")) {
                if (!lineParts[0].equals("null")) {
                    setOwner(UUID.fromString(lineParts[0].trim()));
                }
            } else if (identifier.equalsIgnoreCase("UUID")) {
                uniqueId = new ObjectId(lineParts[0].trim());
            } else if (identifier.equalsIgnoreCase("Members")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        addMember(UUID.fromString(name.trim()));
                    }
                }
            } else if(identifier.equalsIgnoreCase("CoLeaders")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        addCoLeader(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Captains")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        addCaptain(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Invited")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        getInvitations().add(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("HistoricalMembers")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        getHistoricalMembers().add(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("HQ")) {
                setHQ(parseLocation(lineParts));
            } else if (identifier.equalsIgnoreCase("DTR")) {
                setDTR(Double.parseDouble(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Balance")) {
                setBalance(Double.parseDouble(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("MaxOnline")) {
                setMaxOnline(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("ForceInvites")) {
                setForceInvites(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("DTRCooldown")) {
                setDTRCooldown(Long.parseLong(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("FriendlyName")) {
                setName(lineParts[0]);
            } else if (identifier.equalsIgnoreCase("Claims")) {
                for (String claim : lineParts) {
                    claim = claim.replace("[", "").replace("]", "");

                    if (claim.contains(":")) {
                        String[] split = claim.split(":");

                        int x1 = Integer.parseInt(split[0].trim());
                        int y1 = Integer.parseInt(split[1].trim());
                        int z1 = Integer.parseInt(split[2].trim());
                        int x2 = Integer.parseInt(split[3].trim());
                        int y2 = Integer.parseInt(split[4].trim());
                        int z2 = Integer.parseInt(split[5].trim());
                        String name = split[6].trim();
                        String world = split[7].trim();

                        Claim claimObj = new Claim(world, x1, y1, z1, x2, y2, z2);
                        claimObj.setName(name);

                        getClaims().add(claimObj);
                    }
                }
            } else if (identifier.equalsIgnoreCase("Allies")) {
                // Just cancel loading of allies if they're disabled (for switching # of allowed allies mid-map)
                if (Foxtrot.getInstance().getMapHandler().getAllyLimit() == 0) {
                    continue;
                }

                for (String ally : lineParts) {
                    ally = ally.replace("[", "").replace("]", "");

                    if (ally.length() != 0) {
                        allies.add(new ObjectId(ally.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("RequestedAllies")) {
                // Just cancel loading of allies if they're disabled (for switching # of allowed allies mid-map)
                if (Foxtrot.getInstance().getMapHandler().getAllyLimit() == 0) {
                    continue;
                }

                for (String requestedAlly : lineParts) {
                    requestedAlly = requestedAlly.replace("[", "").replace("]", "");

                    if (requestedAlly.length() != 0) {
                        requestedAllies.add(new ObjectId(requestedAlly.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Subclaims")) {
                for (String subclaim : lineParts) {
                    subclaim = subclaim.replace("[", "").replace("]", "");

                    if (subclaim.contains(":")) {
                        String[] split = subclaim.split(":");

                        int x1 = Integer.parseInt(split[0].trim());
                        int y1 = Integer.parseInt(split[1].trim());
                        int z1 = Integer.parseInt(split[2].trim());
                        int x2 = Integer.parseInt(split[3].trim());
                        int y2 = Integer.parseInt(split[4].trim());
                        int z2 = Integer.parseInt(split[5].trim());
                        String name = split[6].trim();
                        String membersRaw = "";

                        if (split.length >= 8) {
                            membersRaw = split[7].trim();
                        }

                        Location location1 = new Location(Foxtrot.getInstance().getServer().getWorld("world"), x1, y1, z1);
                        Location location2 = new Location(Foxtrot.getInstance().getServer().getWorld("world"), x2, y2, z2);
                        List<UUID> members = new ArrayList<>();

                        for (String uuidString : membersRaw.split(", ")) {
                            if (uuidString.isEmpty()) {
                                continue;
                            }

                            members.add(UUID.fromString(uuidString.trim()));
                        }

                        Subclaim subclaimObj = new Subclaim(location1, location2, name);
                        subclaimObj.setMembers(members);

                        getSubclaims().add(subclaimObj);
                    }
                }
            } else if (identifier.equalsIgnoreCase("Announcement")) {
                setAnnouncement(lineParts[0]);
            } else if(identifier.equalsIgnoreCase("PowerFaction")) {
                setPowerFaction(Boolean.valueOf(lineParts[0]));
            } else if(identifier.equalsIgnoreCase("Lives")) {
                setLives(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Kills")) {
                setKills(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Deaths")) {
                setDeaths(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("KothCaptures")) {
                setKothCaptures(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("DiamondsMined")) {
                setDiamondsMined(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("CitadelsCapped")) {
                setCitadelsCapped(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("KillstreakPoints")) {
                setKillstreakPoints(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("PlaytimePoints")) {
                setPlaytimePoints(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Points")) {
	            setPoints(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("SpentPoints")) {
				setSpentPoints(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("SpawnersInClaim")) {
                setSpawnersInClaim(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Upgrades")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        String[] nameSplit = name.split(";");
                        upgradeToTier.put(nameSplit[0].trim(), Integer.valueOf(nameSplit[1].trim()));
                    }
                }
            }
        }

        for (UUID member : members) {
            FrozenUUIDCache.ensure(member);
        }

        if (uniqueId == null) {
            uniqueId = new ObjectId();
            Foxtrot.getInstance().getLogger().info("Generating UUID for team " + getName() + "...");
        }

        loading = false;
        needsSave = forceSave;
    }

    public String saveString(boolean toJedis) {
        if (toJedis) {
            needsSave = false;
        }

        if (loading) {
            return (null);
        }

        StringBuilder teamString = new StringBuilder();

        StringBuilder members = new StringBuilder();
        StringBuilder captains = new StringBuilder();
        StringBuilder coleaders = new StringBuilder();
        StringBuilder invites = new StringBuilder();
        StringBuilder historicalMembers = new StringBuilder();

        for (UUID member : getMembers()) {
            members.append(member.toString()).append(", ");
        }

        for (UUID captain : getCaptains()) {
            captains.append(captain.toString()).append(", ");
        }

        for (UUID co : getColeaders()) {
            coleaders.append(co.toString()).append(", ");
        }

        for (UUID invite : getInvitations()) {
            invites.append(invite.toString()).append(", ");
        }

        for (UUID member : getHistoricalMembers()) {
            historicalMembers.append(member.toString()).append(", ");
        }

        if (members.length() > 2) {
            members.setLength(members.length() - 2);
        }

        if (captains.length() > 2) {
            captains.setLength(captains.length() - 2);
        }

        if (invites.length() > 2) {
            invites.setLength(invites.length() - 2);
        }

        if (historicalMembers.length() > 2) {
            historicalMembers.setLength(historicalMembers.length() - 2);
        }

        StringBuilder upgrades = new StringBuilder();

        for (Map.Entry<String, Integer> entry : upgradeToTier.entrySet()) {
            upgrades.append(entry.getKey()).append(";").append(entry.getValue()).append(", ");
        }

        if (upgrades.length() > 2) {
            upgrades.setLength(upgrades.length() - 2);
        }

        teamString.append("UUID:").append(getUniqueId().toString()).append("\n");
        teamString.append("Owner:").append(getOwner()).append('\n');
        teamString.append("CoLeaders:").append(coleaders).append('\n');
        teamString.append("Captains:").append(captains).append('\n');
        teamString.append("Members:").append(members).append('\n');
        teamString.append("Invited:").append(invites.toString().replace("\n", "")).append('\n');
        teamString.append("Subclaims:").append(getSubclaims().toString().replace("\n", "")).append('\n');
        teamString.append("Claims:").append(getClaims().toString().replace("\n", "")).append('\n');
        teamString.append("Allies:").append(getAllies().toString()).append('\n');
        teamString.append("RequestedAllies:").append(getRequestedAllies().toString()).append('\n');
        teamString.append("HistoricalMembers:").append(historicalMembers).append('\n');
        teamString.append("DTR:").append(getDTR()).append('\n');
        teamString.append("Balance:").append(getBalance()).append('\n');
        teamString.append("MaxOnline:").append(getMaxOnline()).append('\n');
        teamString.append("ForceInvites:").append(getForceInvites()).append('\n');
        teamString.append("DTRCooldown:").append(getDTRCooldown()).append('\n');
        teamString.append("FriendlyName:").append(getName().replace("\n", "")).append('\n');
        teamString.append("Announcement:").append(String.valueOf(getAnnouncement()).replace("\n", "")).append("\n");
        teamString.append("PowerFaction:").append(String.valueOf(isPowerFaction())).append("\n");
        teamString.append("Lives:").append(String.valueOf(getLives())).append("\n");
        teamString.append("Kills:").append(String.valueOf(getKills())).append("\n");
        teamString.append("Deaths:").append(String.valueOf(getDeaths())).append("\n");
        teamString.append("DiamondsMined:").append(String.valueOf(getDiamondsMined())).append("\n");
        teamString.append("KothCaptures:").append(String.valueOf(getKothCaptures())).append("\n");
        teamString.append("CitadelsCapped:").append(String.valueOf(getCitadelsCapped())).append("\n");
        teamString.append("KillstreakPoints:").append(getKillstreakPoints()).append("\n");
        teamString.append("PlaytimePoints:").append(String.valueOf(getPlaytimePoints())).append("\n");
	    teamString.append("Points:").append(String.valueOf(getPoints())).append("\n");
	    teamString.append("SpentPoints:").append(String.valueOf(getSpentPoints())).append("\n");
        teamString.append("SpawnersInClaim:").append(String.valueOf(getSpawnersInClaim())).append("\n");
        teamString.append("Upgrades:").append(upgrades.toString()).append("\n");

        if (getHQ() != null) {
            teamString.append("HQ:").append(getHQ().getWorld().getName()).append(",").append(getHQ().getX()).append(",").append(getHQ().getY()).append(",").append(getHQ().getZ()).append(",").append(getHQ().getYaw()).append(",").append(getHQ().getPitch()).append('\n');
        }

        return (teamString.toString());
    }

    public BasicDBObject toJSON() {
        BasicDBObject dbObject = new BasicDBObject();
        
        dbObject.put("Owner", getOwner() == null ? null : getOwner().toString());
        dbObject.put("CoLeaders", UUIDUtils.uuidsToStrings(getColeaders()));
        dbObject.put("Captains", UUIDUtils.uuidsToStrings(getCaptains()));
        dbObject.put("Members", UUIDUtils.uuidsToStrings(getMembers()));
        dbObject.put("Invitations", UUIDUtils.uuidsToStrings(getInvitations()));
        dbObject.put("Allies", getAllies());
        dbObject.put("RequestedAllies", getRequestedAllies());
        dbObject.put("DTR", getDTR());
        dbObject.put("DTRCooldown", new Date(getDTRCooldown()));
        dbObject.put("Balance", getBalance());
        dbObject.put("MaxOnline", getMaxOnline());
        dbObject.put("Name", getName());
        dbObject.put("HQ", LocationSerializer.serialize(getHQ()));
        dbObject.put("Announcement", getAnnouncement());
        dbObject.put("PowerFaction", isPowerFaction());
        dbObject.put("Lives", getLives());

        BasicDBList claims = new BasicDBList();
        BasicDBList subclaims = new BasicDBList();

        for (Claim claim : getClaims()) {
            claims.add(claim.json());
        }

        for (Subclaim subclaim : getSubclaims()) {
            subclaims.add(subclaim.json());
        }

        dbObject.put("Claims", claims);
        dbObject.put("Subclaims", subclaims);
        dbObject.put("Kills", this.kills);
        dbObject.put("Deaths", this.deaths);
        dbObject.put("DiamondsMined", this.diamondsMined);
        dbObject.put("CitadelsCaptured", this.citadelsCapped);
        dbObject.put("KillstreakPoints", this.killstreakPoints);
        dbObject.put("PlaytimePoints", this.playtimePoints);
        dbObject.put("KothCaptures", this.kothCaptures);
	    dbObject.put("Points", this.points);
	    dbObject.put("SpentPoints", this.spentPoints);
	    dbObject.put("SpawnersInClaim", this.spawnersInClaim);

        BasicDBList upgrades = new BasicDBList();

        for (Map.Entry<String, Integer> entry : upgradeToTier.entrySet()) {
            BasicDBObject upgradeDBObject = new BasicDBObject();
            upgradeDBObject.put("UpgradeName", entry.getKey());
            upgradeDBObject.put("Tier", entry.getValue());

            upgrades.add(upgradeDBObject);
        }

	    dbObject.put("Upgrades", upgrades);

        return (dbObject);
    }

    public BasicDBObject getJSONIdentifier() {
        return (new BasicDBObject("_id", getUniqueId().toHexString()));
    }

    private Location parseLocation(String[] args) {
        if (args.length != 6) {
            return (null);
        }

        World world = Foxtrot.getInstance().getServer().getWorld(args[0]);
        double x = Double.parseDouble(args[1]);
        double y = Double.parseDouble(args[2]);
        double z = Double.parseDouble(args[3]);
        float yaw = Float.parseFloat(args[4]);
        float pitch = Float.parseFloat(args[5]);

        return (new Location(world, x, y, z, yaw, pitch));
    }

    public void sendMessage(String message) {
        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (isMember(player.getUniqueId())) {
                player.sendMessage(CC.translate(message));
            }
        }
    }

    public void sendTeamInfo(Player player) {
        // Don't make our null teams have DTR....
        // @HCFactions
        if (getOwner() == null) {
            player.sendMessage(GRAY_LINE);
            player.sendMessage(getName(player));

            if ( HQ != null && HQ.getWorld().getEnvironment() != World.Environment.NORMAL) {
                String world = HQ.getWorld().getEnvironment() == World.Environment.NETHER ? "Nether" : "End"; // if it's not the nether, it's the end
                player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ() + " (" + world + ")"));
            } else {
                player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));
            }

            if (getName().equalsIgnoreCase("Citadel")) {
                Set<ObjectId> cappers = Foxtrot.getInstance().getCitadelHandler().getCappers();
                Set<String> capperNames = new HashSet<>();

                for (ObjectId capper : cappers) {
                    Team capperTeam = Foxtrot.getInstance().getTeamHandler().getTeam(capper);

                    if (capperTeam != null) {
                        capperNames.add(capperTeam.getName());
                    }
                }

                if (!cappers.isEmpty()) {
                    player.sendMessage(ChatColor.YELLOW + "Currently captured by: " + ChatColor.RED + Joiner.on(", ").join(capperNames));
                }
            }

            player.sendMessage(GRAY_LINE);
            return;
        }

        //KillsMap killsMap = Foxtrot.getInstance().getKillsMap();
        StatsHandler sm = Foxtrot.getInstance().getMapHandler().getStatsHandler();
        DeathbanMap deathbanMap = Foxtrot.getInstance().getDeathbanMap();
        Player owner = Foxtrot.getInstance().getServer().getPlayer(getOwner());
        StringBuilder allies = new StringBuilder();

        ComponentBuilder coleadersJson = new ComponentBuilder("Co-Leaders: ").color(ChatColor.YELLOW.asBungee());

        ComponentBuilder captainsJson = new ComponentBuilder("Captains: ").color(ChatColor.YELLOW.asBungee());

        if (player.hasPermission("foxtrot.manage")) {
            captainsJson.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/manageteam demote " + getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to demote captains").color(ChatColor.BLUE.asBungee()).create()));
        }

        ComponentBuilder membersJson = new ComponentBuilder("Members:").color(ChatColor.YELLOW.asBungee());

        if (player.hasPermission("foxtrot.manage")) {
            membersJson.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/manageteam promote " + getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bClick to promote members").create()));
        }

        int onlineMembers = 0;

        for (ObjectId allyId : getAllies()) {
            Team ally = Foxtrot.getInstance().getTeamHandler().getTeam(allyId);

            if (ally != null) {
                allies.append(ally.getName(player)).append(ChatColor.YELLOW).append("[").append(ChatColor.GREEN).append(ally.getOnlineMemberAmount()).append("/").append(ally.getSize()).append(ChatColor.YELLOW).append("]").append(ChatColor.GRAY).append(", ");
            }
        }


        for (Player onlineMember : getOnlineMembers()) {
            onlineMembers++;

            // There can only be one owner, so we special case it.
            if (isOwner(onlineMember.getUniqueId())) {
                continue;
            }

            ComponentBuilder appendTo = membersJson;
            if (isCoLeader(onlineMember.getUniqueId())) {
                appendTo = coleadersJson;
            } else if(isCaptain(onlineMember.getUniqueId())) {
                appendTo = captainsJson;
            }

            if (!ChatColor.stripColor(appendTo.toString()).endsWith("s: ")) {
                if (appendTo == coleadersJson && coleaders.size() > 1){
                    appendTo.append(", ").color(ChatColor.GRAY.asBungee());
                } else if (appendTo == captainsJson && captains.size() > 1){
                    appendTo.append(", ").color(ChatColor.GRAY.asBungee());
                } else  if (appendTo == membersJson && members.size() > 1){
                    appendTo.append(" ").color(ChatColor.GRAY.asBungee());
                }
            }

            appendTo.append(onlineMember.getName()).color(ChatColor.GREEN.asBungee()).append("[").color(ChatColor.YELLOW.asBungee());
            appendTo.append(sm.getStats(onlineMember.getUniqueId()).getKills() + "").color(ChatColor.GREEN.asBungee());
            appendTo.append("]").color(ChatColor.YELLOW.asBungee());
        }

        for (UUID offlineMember : getOfflineMembers()) {
            if (isOwner(offlineMember)) {
                continue;
            }

            ComponentBuilder appendTo = membersJson;
            if (isCoLeader(offlineMember)) {
                appendTo = coleadersJson;
            } else if(isCaptain(offlineMember)) {
                appendTo = captainsJson;
            }

            if (!ChatColor.stripColor(appendTo.toString()).endsWith("s: ")) {
                if (appendTo == coleadersJson && coleaders.size() > 1){
                    appendTo.append(", ").color(ChatColor.GRAY.asBungee());
                } else if (appendTo == captainsJson && captains.size() > 1){
                    appendTo.append(", ").color(ChatColor.GRAY.asBungee());
                } else  if (appendTo == membersJson && members.size() > 1){
                    appendTo.append(" ").color(ChatColor.GRAY.asBungee());
                }
            }

            appendTo.append(UUIDUtils.name(offlineMember)).color(deathbanMap.isDeathbanned(offlineMember) ? ChatColor.RED.asBungee() : ChatColor.GRAY.asBungee());
            appendTo.append("[").color(ChatColor.YELLOW.asBungee()).append("" + sm.getStats(offlineMember).getKills()).color(ChatColor.GREEN.asBungee());
            appendTo.append("]").color(ChatColor.YELLOW.asBungee());

        }

        // Now we can actually send all that info we just processed.
        player.sendMessage(GRAY_LINE);

        ComponentBuilder teamLine = new ComponentBuilder();

        teamLine.append(ChatColor.BLUE + getName());
        teamLine.append(ChatColor.GRAY + " [" + onlineMembers + "/" + getSize() + "]" + ChatColor.DARK_AQUA + " - ");
        teamLine.append(ChatColor.YELLOW + "HQ: " + ChatColor.WHITE + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));

        if (HQ != null && player.hasPermission("basic.staff")) {
            teamLine.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tppos " + HQ.getBlockX() + " " + HQ.getBlockY() + " " + HQ.getBlockZ())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick to warp to HQ").create()));
        }

        teamLine.append("§3 - §e[Focus]").color(ChatColor.YELLOW.asBungee())
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f focus " + getName()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bClick to focus team").create()));

        player.spigot().sendMessage(teamLine.create());

        if (allies.length() > 2) {
            allies.setLength(allies.length() - 2);
            player.sendMessage(ChatColor.YELLOW + "Allies: " + allies.toString());
        }

        ComponentBuilder leader = new ComponentBuilder(ChatColor.YELLOW + "Leader: " + (owner == null || owner.hasMetadata("invisible") ? (deathbanMap.isDeathbanned(getOwner()) ? ChatColor.RED : ChatColor.GRAY) : ChatColor.GREEN) + UUIDUtils.name(getOwner()) + ChatColor.YELLOW + "[" + ChatColor.GREEN + sm.getStats(getOwner()).getKills() + ChatColor.YELLOW + "]");


        if (player.hasPermission("foxtrot.manage")) {
            leader.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/manageteam leader " + getName()));
        }

        player.spigot().sendMessage(leader.create());

        if (!ChatColor.stripColor(coleadersJson.toString()).endsWith("s: ") && !(coleaders.size() <= 0)) {
            player.spigot().sendMessage(coleadersJson.create());
        }

        if (!ChatColor.stripColor(captainsJson.toString()).endsWith("s: ") && !(captains.size() <= 0)) {
            player.spigot().sendMessage(captainsJson.create());
        }


        if (!ChatColor.stripColor(membersJson.toString()).endsWith("s: ") && !membersJson.getCurrentComponent().toPlainText().equals("Members:")) {
            player.spigot().sendMessage(membersJson.create());
        }


        ComponentBuilder balance = new ComponentBuilder(ChatColor.YELLOW + "Balance: " + ChatColor.BLUE + "$" + Math.round(getBalance()));

        if (player.hasPermission("foxtrot.manage")) {
            balance.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/manageteam balance " + getName()));
        }

        player.spigot().sendMessage(balance.create());


        ComponentBuilder dtrMessage = new ComponentBuilder(ChatColor.YELLOW + "Deaths until Raidable: " + getDTRColor() + DTR_FORMAT.format(getDTR()) + getDTRSuffix());

        if (player.hasPermission("foxtrot.manage")) {
            dtrMessage.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/manageteam dtr " + getName()));
        }

        player.spigot().sendMessage(dtrMessage.create());

        if (isMember(player.getUniqueId()) || player.hasPermission("foxtrot.manage")) {
            if (Foxtrot.getInstance().getServerHandler().isForceInvitesEnabled()) {
                player.sendMessage(ChatColor.YELLOW + "Force Invites: " + ChatColor.RED + getForceInvites());
            }
            player.sendMessage(ChatColor.YELLOW + "Points: " + ChatColor.RED + getPoints());
            player.sendMessage(ChatColor.YELLOW + "KOTH Captures: " + ChatColor.RED + getKothCaptures());
            player.sendMessage(ChatColor.YELLOW + "Lives: " + ChatColor.RED + getLives());
            player.sendMessage(ChatColor.YELLOW + "Spawners: " + ChatColor.RED + getSpawnersInClaim());
        }

        if (DTRHandler.isOnCooldown(this)) {
            player.sendMessage(ChatColor.YELLOW + "Time Until Regen: " + ChatColor.BLUE + TimeUtils.formatIntoDetailedString(((int) (getDTRCooldown() - System.currentTimeMillis())) / 1000).trim());
        }

        // Only show this if they're a member.
        if (isMember(player.getUniqueId()) && announcement != null && !announcement.equals("null")) {
            player.sendMessage(ChatColor.YELLOW + "Announcement: " + ChatColor.LIGHT_PURPLE + announcement);
        }

        player.sendMessage(GRAY_LINE);
        // .... and that is how we do a /f who.
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Team)) {
            return false;
        }

        Team other = (Team) obj;
        return other.uniqueId.equals(uniqueId);
    }

    public ChatColor getDTRColor() {
        ChatColor dtrColor = ChatColor.GREEN;

        if (DTR / getMaxDTR() <= 0.25) {
            if (isRaidable()) {
                dtrColor = ChatColor.DARK_RED;
            } else {
                dtrColor = ChatColor.YELLOW;
            }
        }

        return (dtrColor);
    }

    public String getDTRSuffix() {
        if (DTRHandler.isRegenerating(this)) {
            if (getOnlineMemberAmount() == 0) {
                return (ChatColor.GRAY + "◀");
            } else {
                return (ChatColor.GREEN + "▲");
            }
        } else if (DTRHandler.isOnCooldown(this)) {
            return (ChatColor.RED + "■");
        } else {
            return (ChatColor.GREEN + "◀");
        }
    }

}

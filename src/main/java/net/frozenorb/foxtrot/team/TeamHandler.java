package net.frozenorb.foxtrot.team;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.commands.team.TeamCommands;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeamHandler {
    
    private Map<String, Team> teamNameMap = new ConcurrentHashMap<>(); // Team Name -> Team
    private Map<ObjectId, Team> teamUniqueIdMap = new ConcurrentHashMap<>(); // Team UUID -> Team
    private Map<UUID, Team> playerTeamMap = new ConcurrentHashMap<>(); // Player UUID -> Team
    @Getter
    @Setter
    private boolean rostersLocked = false;
    @Getter
    private static HashSet<Team> powerFactions = new HashSet<>();
    
    public TeamHandler() {
        powerFactions = new HashSet<>();
        
        // Load teams from Redis.
        HCF.getInstance().runRedisCommand(redis -> {
            for (String key : redis.keys("fox_teams.*")) {
                String loadString = redis.get(key);

                try {
                    Team team = new Team(key.split("\\.")[1]);
                    team.load(loadString);

                    setupTeam(team);
                } catch (Exception e) {
                    e.printStackTrace();
                    HCF.getInstance().getLogger().severe("Could not load team from raw string: " + loadString);
                }
            }

            rostersLocked = Boolean.parseBoolean(redis.get("RostersLocked"));
            return (null);
        });
        
        Bukkit.getLogger().info("Creating indexes...");

        MongoCollection<Document> playerCollection = HCF.getInstance().getMongoPool().getDatabase(HCF.MONGO_DB_NAME).getCollection("Players");
        playerCollection.createIndex(new BasicDBObject("Team", 1));

        MongoCollection<Document> teamCollection = HCF.getInstance().getMongoPool().getDatabase(HCF.MONGO_DB_NAME).getCollection("Teams");
        teamCollection.createIndex(new BasicDBObject("Owner", 1));
        teamCollection.createIndex(new BasicDBObject("CoLeaders", 1));
        teamCollection.createIndex(new BasicDBObject("Captains", 1));
        teamCollection.createIndex(new BasicDBObject("Members", 1));
        teamCollection.createIndex(new BasicDBObject("Name", 1));
        teamCollection.createIndex(new BasicDBObject("PowerFaction", 1));

        Bukkit.getLogger().info("Creating indexes done.");
    }
    
    public static void addPowerFaction(Team t) {
        powerFactions.add(t);
    }
    
    public static void removePowerFaction(Team t) {
        powerFactions.remove(t);
    }
    
    public static boolean isPowerFaction(Team t) {
        return powerFactions.contains(t);
    }
    
    public List<Team> getTeams() {
        return (new ArrayList<>(teamNameMap.values()));
    }
    
    public Team getTeam(String teamName) {
        return (teamNameMap.get(teamName.toLowerCase()));
    }
    
    public Team getTeam(ObjectId teamUUID) {
        return (teamUUID == null ? null : teamUniqueIdMap.get(teamUUID));
    }
    
    public Team getTeam(UUID playerUUID) {
        return (playerUUID == null ? null : playerTeamMap.get(playerUUID));
    }
    
    public Team getTeam(Player player) {
        return (getTeam(player.getUniqueId()));
    }
    
    public void setTeam(UUID playerUUID, Team team, boolean update) {
        if (team == null) {
            playerTeamMap.remove(playerUUID);
        } else {
            playerTeamMap.put(playerUUID, team);
        }
        
        if (update) {
            Bukkit.getScheduler().runTaskAsynchronously(HCF.getInstance(), () -> {
                // update their team in mongo
                DBCollection playersCollection = HCF.getInstance().getMongoPool().getDB(HCF.MONGO_DB_NAME).getCollection("Players");
                BasicDBObject player = new BasicDBObject("_id", playerUUID.toString().replace("-", ""));
                
                if (team != null) {
                    playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject("Team", team.getUniqueId().toHexString())));
                } else {
                    playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject("Team", null)));
                }
            });
        }
    }


    
    public void setTeam(UUID playerUUID, Team team) {
        setTeam(playerUUID, team, true); // standard cases we do update mongo
    }
    
    public void setupTeam(Team team) {
        setupTeam(team, false);
    }
    
    public void setupTeam(Team team, boolean update) {
        teamNameMap.put(team.getName().toLowerCase(), team);
        teamUniqueIdMap.put(team.getUniqueId(), team);
        
        for (UUID member : team.getMembers()) {
            setTeam(member, team, update); // no need to update mongo!
        }
    }
    
    public void removeTeam(Team team) {
        teamNameMap.remove(team.getName().toLowerCase());
        teamUniqueIdMap.remove(team.getUniqueId());
        
        for (UUID member : team.getMembers()) {
            setTeam(member, null);
        }
    }
    
    public void recachePlayerTeams() {
        playerTeamMap.clear();
        
        for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
            for (UUID member : team.getMembers()) {
                setTeam(member, team);
            }
        }
    }

    public List<Team> sortTeams(){

        LinkedHashMap<Team, Integer> sortedTeamPlayerCount = TeamCommands.getSortedTeams();
        List<Team> teams = new ArrayList<>();

        int index = 0;

        for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {

            if (teamEntry.getKey().getOwner() == null) {
                continue;
            }

            index++;

            if (3 <= index) {
                break;
            }

            teams.add(teamEntry.getKey());
        }

        return teams;
    }
    
}
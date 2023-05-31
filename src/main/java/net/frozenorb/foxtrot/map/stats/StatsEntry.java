package net.frozenorb.foxtrot.map.stats;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.map.stats.command.StatsTopCommand;
import net.frozenorb.foxtrot.team.Team;

import java.util.UUID;

public class StatsEntry {

    @Getter(value = AccessLevel.PROTECTED) private boolean modified;

    @Getter public UUID owner;

    @Getter
    @Setter
    public int kills;

    @Getter
    @Setter
    public int deaths;

    @Getter
    @Setter
    public int killstreak;

    @Getter public int highestKillstreak;

    public StatsEntry(UUID owner) {
        this.owner = owner;
    }

    public void addKill() {
        kills++;
        killstreak++;

        if (highestKillstreak < killstreak) {
            highestKillstreak = killstreak;
        }

        modified = true;
    }

    public void addDeath() {
        deaths++;
        killstreak = 0;

        modified = true;
    }

    public void clear() {
        kills = 0;
        deaths = 0;
        killstreak = 0;
        highestKillstreak = 0;

        modified = true;
    }

    public double getKD() {
        if (getDeaths() == 0) {
            return 0;
        }

        return (double) getKills() / (double) getDeaths();
    }

    public Number get(StatsTopCommand.StatsObjective objective) {
        return switch (objective) {
            case KILLS -> getKills();
            case DEATHS -> getDeaths();
            case HIGHEST_KILLSTREAK -> getHighestKillstreak();
            default -> 0;
        };
    }
}

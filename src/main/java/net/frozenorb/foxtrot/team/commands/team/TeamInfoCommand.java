package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamInfoCommand {

    @Command(value={ "team info", "t info", "f info", "faction info", "fac info", "team who", "t who", "f who", "faction who", "fac who", "team show", "t show", "f show", "faction show", "fac show", "team i", "t i", "f i", "faction i", "fac i" }, async = true)
    public static void teamInfo(@Sender final Player sender, @Optional("self") final Team team) {


        new BukkitRunnable() {

            public void run() {
                Team exactPlayerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(team.getName()));

                if (exactPlayerTeam != null && exactPlayerTeam != team) {
                    exactPlayerTeam.sendTeamInfo(sender);
                }

                team.sendTeamInfo(sender);
            }
        }.runTask(Foxtrot.getInstance());
    }

}
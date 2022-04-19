package net.frozenorb.foxtrot.team.commands.team;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.EOTWCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.CC;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.regex.Pattern;

public class TeamCreateCommand {

    public static final Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    private static final Set<String> disallowedTeamNames = ImmutableSet.of("list", "Glowstone", "self");

    @Command(value = {"team create", "t create", "f create", "faction create", "fac create"})
    public static void teamCreate(@Sender Player sender, @Name("team") String team) {
        if (Foxtrot.getInstance().getTeamHandler().getTeam(sender) != null) {
            sender.sendMessage(ChatColor.GRAY + "You're already in a team!");
            return;
        }

        if (team.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
            return;
        }

        if (team.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
            return;
        }

//        if (TeamGeneralConfiguration.getDisallowedNames().contains(team.toLowerCase()) && !sender.isOp()) {
//            sender.sendMessage(ChatColor.RED + "That faction name is not allowed.");
//            return;
//        }


        if (Foxtrot.getInstance().getTeamHandler().getTeam(team) != null) {
            sender.sendMessage(ChatColor.GRAY + "That team already exists!");
            return;
        }


        if (disallowedTeamNames.contains(team.toLowerCase())) {
            sender.sendMessage(CC.translate("&cThat team name is not allowed."));
            return;
        }

        if (ALPHA_NUMERIC.matcher(team).find()) {
            sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW()){
            sender.sendMessage(CC.translate("&cThe server is in EOTW Mode: Teams cannot be create."));
        }

        if (EOTWCommand.realFFAStarted()) {
            sender.sendMessage(ChatColor.RED + "You can't create teams during FFA.");
            return;
        }

        // sender.sendMessage(ChatColor.DARK_AQUA + "Team Created!");
        sender.sendMessage(ChatColor.GRAY + "To learn more about teams, do /team");

        Team createdTeam = new Team(team);

        TeamActionTracker.logActionAsync(createdTeam, TeamActionType.PLAYER_CREATE_TEAM, ImmutableMap.of(
                "playerId", sender.getUniqueId(),
                "playerName", sender.getName()
        ));

        createdTeam.setUniqueId(new ObjectId());
        createdTeam.setOwner(sender.getUniqueId());
        createdTeam.setName(team);
        createdTeam.setDTR(1);

        Foxtrot.getInstance().getTeamHandler().setupTeam(createdTeam);

        sender.sendMessage(ChatColor.YELLOW + "Team " + ChatColor.BLUE + createdTeam.getName() + ChatColor.YELLOW + " has been " + ChatColor.GREEN + "created" + ChatColor.YELLOW + " by " + sender.getDisplayName());
    }

}
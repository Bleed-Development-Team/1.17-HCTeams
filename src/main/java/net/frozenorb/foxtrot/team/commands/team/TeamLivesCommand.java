package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamLivesCommand {

    @Command(value ={ "team lives add", "t lives add", "f lives add", "fac lives add", "faction lives add", "t lives deposit", "t lives d", "f lives deposit", "f lives d" })
    public static void livesAdd(@Sender Player sender, @Name("lives") int lives) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        if( team == null ) {
            sender.sendMessage(ChatColor.RED + "You need a team to use this command.");
            return;
        }

        if( lives <= 0 ) {
            sender.sendMessage(ChatColor.RED + "You really think we'd fall for that?");
            return;
        }

        int currLives = Foxtrot.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());

        if( currLives < lives ) {
            sender.sendMessage(ChatColor.RED + "You only have " + ChatColor.YELLOW + currLives + ChatColor.RED + " friend lives, you cannot deposit " + ChatColor.YELLOW + lives);
            return;
        }

        Foxtrot.getInstance().getFriendLivesMap().setLives(sender.getUniqueId(), currLives - lives);
        team.addLives(lives);
        sender.sendMessage(ChatColor.GREEN + "You have deposited " + ChatColor.RED + lives + ChatColor.GREEN + "  friendlives to " + ChatColor.YELLOW + team.getName() + ChatColor.GREEN + ". You now have " + ChatColor.RED + (currLives - lives) + ChatColor.GREEN + " lives and your team now has " + ChatColor.RED + team.getLives() + ChatColor.GREEN + " lives." );
    }

    @Command(value={ "team revive", "t revive", "f revive", "fac revive", "faction revive" })
    public static void livesRevive(@Sender Player sender, @Name("player") UUID whom) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        if( team == null ) {
            sender.sendMessage(ChatColor.RED + "You need a team to use this command.");
            return;
        }

        if(!team.isCoLeader(sender.getUniqueId()) && !team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only co-leaders and owners can use this command!");
            return;
        }

        if(team.getLives() <= 0) {
            sender.sendMessage(ChatColor.RED + "Your team has no lives to use.");
            return;
        }

        if(!team.isMember(whom)) {
            sender.sendMessage(ChatColor.RED + "This player is not a member of your team.");
            return;
        }

        if(!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(whom)) {
            sender.sendMessage(ChatColor.RED + "This player is not death banned currently.");
            return;
        }

        team.removeLives(1);
        Foxtrot.getInstance().getDeathbanMap().revive(whom);
        sender.sendMessage(ChatColor.GREEN + "You have revived " + ChatColor.RED + UUIDUtils.name(whom) + ChatColor.GREEN + ".");
    }

    @Command(value={ "team lives", "t lives", "f lives", "fac lives", "faction lives" })
    public static void getLives(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        if( team == null ) {
            sender.sendMessage(ChatColor.RED + "You need a team to use this command.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Your team has " + ChatColor.RED + team.getLives() + ChatColor.YELLOW + " lives.");
        sender.sendMessage(ChatColor.YELLOW + "To deposit lives, use /t lives add <amount>");
        sender.sendMessage(ChatColor.YELLOW + "Life deposits are FINAL!");
        sender.sendMessage(ChatColor.YELLOW + "Leaders can revive members using " + ChatColor.WHITE + "/t revive <name>");
    }
}

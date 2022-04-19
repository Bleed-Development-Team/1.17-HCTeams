package net.frozenorb.foxtrot.team.commands.team;

import com.google.common.collect.ImmutableMap;
import me.vaperion.blade.annotation.*;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.event.FullTeamBypassEvent;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;

import net.frozenorb.foxtrot.util.UUIDUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamInviteCommand {

    @Command(value={ "team invite", "t invite", "f invite", "faction invite", "fac invite", "team inv", "t inv", "f inv", "faction inv", "fac inv" })
    public static void teamInvite(@Sender Player sender, @Name("player") Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMembers().size() >= Foxtrot.getInstance().getMapHandler().getTeamSize()) {
            FullTeamBypassEvent bypassEvent = new FullTeamBypassEvent(sender, team);
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(bypassEvent);

            if (!bypassEvent.isAllowBypass()) {
                sender.sendMessage(ChatColor.RED + "The max team size is " + Foxtrot.getInstance().getMapHandler().getTeamSize() + (bypassEvent.getExtraSlots() == 0 ? "" : " (+" + bypassEvent.getExtraSlots() + ")") + "!");
                return;
            }
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + player.getName() + " is already on your team.");
            return;
        }

        if (team.getInvitations().contains(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player has already been invited.");
            return;
        }

        /*if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not invite players while your team is raidable!");
            return;
        }*/

        if (Foxtrot.getInstance().getServerHandler().isForceInvitesEnabled() && !Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
            /* if we just check team.getSize() players can make a team with 10 players,
            send out 20 invites, and then have them all accepted (instead of 1 invite,
            1 join, 1 invite, etc) To solve this we treat their size as their actual
            size + number of open invites. */
            int possibleTeamSize = team.getSize() + team.getInvitations().size();

            if (!Foxtrot.getInstance().getMapHandler().isKitMap() && !Foxtrot.getInstance().getServerHandler().isVeltKitMap() && team.getHistoricalMembers().contains(player.getUniqueId()) && possibleTeamSize > Foxtrot.getInstance().getMapHandler().getMinForceInviteMembers()) {
                sender.sendMessage(ChatColor.RED + "This player has previously joined your faction. You must use a force-invite to re-invite " + player.getName() + ". Type "
                        + ChatColor.YELLOW + "'/f forceinvite " + player.getName() + "'" + ChatColor.RED + " to use a force-invite."
                );

                return;
            }
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_INVITE_SENT, ImmutableMap.of(
                "playerId", player,
                "invitedById", sender.getUniqueId(),
                "invitedByName", sender.getName(),
                "betrayOverride", "something other then yeah",
                "usedForceInvite", "false"
        ));

        team.getInvitations().add(player.getUniqueId());
        team.flagForSave();

        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player.getUniqueId());

        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " invited you to join '" + ChatColor.YELLOW + team.getName() + ChatColor.DARK_AQUA + "'.");

            ComponentBuilder clickToJoin = new ComponentBuilder ("Type '").color(ChatColor.DARK_AQUA.asBungee())
            .append("/team join " + team.getName()).color(ChatColor.YELLOW.asBungee());
            clickToJoin.append("' or ").color(ChatColor.DARK_AQUA.asBungee());
            clickToJoin.append("click here").color(ChatColor.AQUA.asBungee())
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f join " + team.getName()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aJoin " + team.getName()).create()));
            clickToJoin.append(" to join.").color(ChatColor.DARK_AQUA.asBungee());

            bukkitPlayer.spigot().sendMessage(clickToJoin.create());
        }

        team.sendMessage(ChatColor.YELLOW + player.getName() + " has been invited to the team!");
    }

}
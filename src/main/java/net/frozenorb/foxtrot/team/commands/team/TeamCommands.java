package net.frozenorb.foxtrot.team.commands.team;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.commands.EOTWCommand;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.*;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.team.event.FullTeamBypassEvent;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.CuboidRegion;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@CommandAlias("team|t|f|faction|fac")
public class TeamCommands extends BaseCommand implements Listener {
    @Getter
    private static Map<UUID, String> teamMutes = new HashMap<>();
    @Getter public static Map<UUID, String> teamShadowMutes = new HashMap<>();

    public static final Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    private static final Set<String> disallowedTeamNames = ImmutableSet.of("list", "Glowstone", "self");

    private static final double MAX_DISTANCE = 5;

    private static final Set<Integer> warn = new HashSet<>();

    @Subcommand("a|accept|join|j")
    @Description("Accepts a team invitation")
    public static void teamAccept(Player sender, @Name("team") Team team) {
        if (team.getInvitations().contains(sender.getUniqueId())) {
            if (Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId()) != null) {
                sender.sendMessage(ChatColor.RED + "You are already on a team!");
                return;
            }

            if (team.getMembers().size() >= Foxtrot.getInstance().getMapHandler().getTeamSize()) {
                FullTeamBypassEvent attemptEvent = new FullTeamBypassEvent(sender, team);
                Foxtrot.getInstance().getServer().getPluginManager().callEvent(attemptEvent);

                if (!attemptEvent.isAllowBypass()) {
                    sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team is full!");
                    return;
                }
            }

            if (DTRHandler.isOnCooldown(team) && !Foxtrot.getInstance().getServerHandler().isPreEOTW() && !Foxtrot.getInstance().getMapHandler().isKitMap() && !Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team not regenerating DTR!");
                return;
            }

            if (team.getMembers().size() >= 15 && Foxtrot.getInstance().getTeamHandler().isRostersLocked()) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team rosters are locked server-wide!");
                return;
            }

            if (SpawnTagHandler.isTagged(sender)) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: You are combat tagged!");
                return;
            }

            if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null){
                LunarClientAPI.getInstance().sendWaypoint(sender, new LCWaypoint(
                        team.getFocusedTeam().getName() + "'s HQ",
                        team.getFocusedTeam().getHQ(),
                        Color.BLUE.hashCode(),
                        true
                ));
            }

            if (team.getTeamRally() != null){
                LunarClientAPI.getInstance().sendWaypoint(sender, new LCWaypoint(
                        "Team Rally",
                        team.getTeamRally(),
                        Color.AQUA.hashCode(),
                        true
                ));
            }

            if (team.getHQ() != null){
                LunarClientAPI.getInstance().sendWaypoint(sender, new LCWaypoint(
                        "HQ",
                        team.getHQ(),
                        Color.BLUE.hashCode(),
                        true
                ));
            }


            team.getInvitations().remove(sender.getUniqueId());
            team.addMember(sender.getUniqueId());
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), team);

            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has joined the team!");

            //FrozenNametagHandler.reloadPlayer(sender);
            //FrozenNametagHandler.reloadOthersFor(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "This team has not invited you!");
        }
    }

    @CommandAlias("ally")
    @Description("Allies a team with another team")
    public static void teamAlly(Player sender, @Name("team") Team team) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(senderTeam.isOwner(sender.getUniqueId()) || senderTeam.isCaptain(sender.getUniqueId()) || senderTeam.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (senderTeam.equals(team)) {
            sender.sendMessage(ChatColor.YELLOW + "You cannot ally your own team!");
            return;
        }

        if (senderTeam.getAllies().size() >= Foxtrot.getInstance().getMapHandler().getAllyLimit()) {
            sender.sendMessage(ChatColor.YELLOW + "Your team already has the max number of allies, which is " + Foxtrot.getInstance().getMapHandler().getAllyLimit() + ".");
            return;
        }

        if (team.getAllies().size() >= Foxtrot.getInstance().getMapHandler().getAllyLimit()) {
            sender.sendMessage(ChatColor.YELLOW + "The team you're trying to ally already has the max number of allies, which is " + Foxtrot.getInstance().getMapHandler().getAllyLimit() + ".");
            return;
        }

        if (senderTeam.isAlly(team)) {
            sender.sendMessage(ChatColor.YELLOW + "You're already allied to " + team.getName(sender) + ChatColor.YELLOW + ".");
            return;
        }

        if (senderTeam.getRequestedAllies().contains(team.getUniqueId())) {
            senderTeam.getRequestedAllies().remove(team.getUniqueId());

            team.getAllies().add(senderTeam.getUniqueId());
            senderTeam.getAllies().add(team.getUniqueId());

            team.flagForSave();
            senderTeam.flagForSave();

            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(senderTeam.getName(player) + ChatColor.YELLOW + " has accepted your request to ally. You now have " + Team.ALLY_COLOR + team.getAllies().size() + "/" + Foxtrot.getInstance().getMapHandler().getAllyLimit() + " allies" + ChatColor.YELLOW + ".");
                } else if (senderTeam.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "Your team has allied " + team.getName(sender) + ChatColor.YELLOW + ". You now have " + Team.ALLY_COLOR + senderTeam.getAllies().size() + "/" + Foxtrot.getInstance().getMapHandler().getAllyLimit() + " allies" + ChatColor.YELLOW + ".");
                }

                if (team.isMember(player.getUniqueId()) || senderTeam.isMember(player.getUniqueId())) {
                    //FrozenNametagHandler.reloadPlayer(sender);
                    // FrozenNametagHandler.reloadOthersFor(sender);
                }
            }
        } else {
            if (team.getRequestedAllies().contains(senderTeam.getUniqueId())) {
                sender.sendMessage(ChatColor.YELLOW + "You have already requested to ally " + team.getName(sender) + ChatColor.YELLOW + ".");
                return;
            }

            team.getRequestedAllies().add(senderTeam.getUniqueId());
            team.flagForSave();

            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(senderTeam.getName(player.getPlayer()) + ChatColor.YELLOW + " has requested to be your ally. Type " + Team.ALLY_COLOR + "/team ally " + senderTeam.getName() + ChatColor.YELLOW + " to accept.");
                } else if (senderTeam.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "Your team has requested to ally " + team.getName(player) + ChatColor.YELLOW + ".");
                }
            }
        }
    }
    @Subcommand("announcement|announcement")
    @Description("Announce a message to all teams.")
    public static void teamAnnouncement(Player sender, @Name("announcement") String newAnnouncement) {//TODO Check if string will work
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (newAnnouncement.equalsIgnoreCase("clear")) {
            team.setAnnouncement(null);
            sender.sendMessage(ChatColor.YELLOW + "Team announcement cleared.");
            return;
        }

        team.setAnnouncement(newAnnouncement);
        team.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW  + " changed the team announcement to " + ChatColor.LIGHT_PURPLE + newAnnouncement);
    }
    @Subcommand("captain add|mod add")
    @Description("Add a player to the mod team.")
    public static void captainAdd(Player sender, @Name("target") OfflinePlayer promote) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if( team == null ) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if(!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Only team co-leaders can execute this command.");
            return;
        }

        if(!team.isMember(promote.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if(team.isOwner(promote.getUniqueId()) || team.isCaptain(promote.getUniqueId()) || team.isCoLeader(promote.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "This player is already a captain (or above) of your team.");
            return;
        }

        team.removeCoLeader(promote.getUniqueId());
        team.addCaptain(promote.getUniqueId());
        team.sendMessage(org.bukkit.ChatColor.DARK_AQUA + UUIDUtils.name(promote.getUniqueId()) + " has been promoted to Captain!");
    }
    @Subcommand("captain remove|captain demote| mod remove|mod demote")
    @Description("Demote a player to a member.")
    public static void captainRemove(Player sender, @Name("target") OfflinePlayer demote) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if( team == null ) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if(!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Only team co-leaders can execute this command.");
            return;
        }

        if(!team.isMember(demote.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if(!team.isCaptain(demote.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "This player is not a captain of your team.");
            return;
        }

        team.removeCoLeader(demote.getUniqueId());
        team.removeCaptain(demote.getUniqueId());
        team.sendMessage(org.bukkit.ChatColor.DARK_AQUA + UUIDUtils.name(demote.getUniqueId()) + " has been demoted to a member!");
    }
    @Subcommand("chat|c")
    @Description("Toggle team chat.")
    public static void teamChat(Player sender, @Optional String chatMode) {
        ChatMode parsedChatMode = null;

        if (chatMode.equalsIgnoreCase("t") || chatMode.equalsIgnoreCase("team") || chatMode.equalsIgnoreCase("f") || chatMode.equalsIgnoreCase("fac") || chatMode.equalsIgnoreCase("faction") || chatMode.equalsIgnoreCase("fc")) {
            parsedChatMode = ChatMode.TEAM;
        } else if (chatMode.equalsIgnoreCase("g") || chatMode.equalsIgnoreCase("p") || chatMode.equalsIgnoreCase("global") || chatMode.equalsIgnoreCase("public") || chatMode.equalsIgnoreCase("gc")) {
            parsedChatMode = ChatMode.PUBLIC;
        } else if (chatMode.equalsIgnoreCase("a") || chatMode.equalsIgnoreCase("allies") || chatMode.equalsIgnoreCase("ally") || chatMode.equalsIgnoreCase("alliance") || chatMode.equalsIgnoreCase("ac")) {
            parsedChatMode = ChatMode.ALLIANCE;
        } else if (chatMode.equalsIgnoreCase("captain") || chatMode.equalsIgnoreCase("officer") || chatMode.equalsIgnoreCase("o") || chatMode.equalsIgnoreCase("c") || chatMode.equalsIgnoreCase("oc")) {
            parsedChatMode = ChatMode.OFFICER;
        }

        setChat(sender, parsedChatMode);
    }


    private static void setChat(Player player, ChatMode chatMode) {
        if (chatMode != null) {
            Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

            if (chatMode != ChatMode.PUBLIC) {
                if (playerTeam == null) {
                    player.sendMessage(ChatColor.RED + "You must be on a team to use this chat mode.");
                    return;
                } else if (chatMode == ChatMode.OFFICER && !playerTeam.isCaptain(player.getUniqueId()) && !playerTeam.isCoLeader(player.getUniqueId()) && !playerTeam.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You must be an officer or above in your team to use this chat mode.");
                    return;
                }
            }

            switch (chatMode) {
                case PUBLIC:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
                    break;
                case ALLIANCE:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in alliance chat.");
                    break;
                case TEAM:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in team chat.");
                    break;
                case OFFICER:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in officer chat.");
                    break;
            }

            Foxtrot.getInstance().getChatModeMap().setChatMode(player.getUniqueId(), chatMode);
        } else {
            switch (Foxtrot.getInstance().getChatModeMap().getChatMode(player.getUniqueId())) {
                case PUBLIC -> {
                    Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
                    boolean teamHasAllies = team != null && team.getAllies().size() > 0;
                    setChat(player, teamHasAllies ? ChatMode.ALLIANCE : ChatMode.TEAM);
                }
                case ALLIANCE -> setChat(player, ChatMode.TEAM);
                case TEAM -> {
                    Team team2 = Foxtrot.getInstance().getTeamHandler().getTeam(player);
                    boolean isOfficer = team2 != null && (team2.isCaptain(player.getUniqueId()) || team2.isCoLeader(player.getUniqueId()) || team2.isOwner(player.getUniqueId()));
                    setChat(player, isOfficer ? ChatMode.OFFICER : ChatMode.PUBLIC);
                }
                case OFFICER -> setChat(player, ChatMode.PUBLIC);
            }
        }
    }
    @Subcommand("create")
    @Description("Create a team")
    public static void teamCreate(Player sender, @Name("name") String team) {
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

        if (EOTWCommand.realFFAStarted()) {
            sender.sendMessage(ChatColor.RED + "You can't create teams during FFA.");
            return;
        }
        //No longer needed cause we don't disband factions
        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You can't create teams during EOTW.");
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

    @Subcommand("demote")
    @Description("Demote a player from team.")
    public static void teamDemote(Player sender, @Name("target") OfflinePlayer player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team co-leaders (and above) can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " is not on your team.");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " is the leader. To change leaders, the team leader must use /t leader <name>");
        } else if (team.isCoLeader(player.getUniqueId())) {
            if (team.isOwner(sender.getUniqueId())) {
                team.removeCoLeader(player.getUniqueId());
                team.addCaptain(player.getUniqueId());
                team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " has been demoted to Captain!");
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can demote Co-Leaders.");
            }
        } else if (team.isCaptain(player.getUniqueId())) {
            team.removeCaptain(player.getUniqueId());
            team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " has been demoted to a Member!");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " is currently a member. To kick them, use /t kick");
        }
    }

    @Subcommand("deposit|d")
    @Description("Deposit money to your team.")
    public static void teamDeposit(Player sender, @Name("amount") String ogAmount) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        float amount = (ogAmount.equals("all")) ? (float) FrozenEconomyHandler.getBalance(sender.getUniqueId())
                : Float.parseFloat(ogAmount);


        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "You can't deposit $0.0 (or less)!");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage(ChatColor.RED + "Nope.");
            return;
        }

        if (FrozenEconomyHandler.getBalance(sender.getUniqueId()) < amount) {
            sender.sendMessage(ChatColor.RED + "You don't have enough money to do this!");
            return;
        }

        FrozenEconomyHandler.withdraw(sender.getUniqueId(), amount);

        sender.sendMessage(ChatColor.YELLOW + "You have added " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " to the team balance!");

        TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_DEPOSIT_MONEY, ImmutableMap.of(
                "playerId", sender.getUniqueId(),
                "playerName", sender.getName(),
                "amount", amount,
                "oldBalance", team.getBalance(),
                "newBalance", team.getBalance() + amount
        ));

        team.setBalance(team.getBalance() + amount);
        team.sendMessage(ChatColor.YELLOW + sender.getName() + " deposited " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " into the team balance.");

        Foxtrot.getInstance().getWrappedBalanceMap().setBalance(sender.getUniqueId(), FrozenEconomyHandler.getBalance(sender.getUniqueId()));
    }

    @Subcommand("disband")
    @Description("Disband your team.")
    public static void teamDisband(Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null){
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the leader of the team to disband it!");
            return;
        }

        if (team.isRaidable()) {
            player.sendMessage(ChatColor.RED + "You cannot disband your team while raidable.");
            return;
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                            team.getFocusedTeam().getName() + "'s HQ",
                            team.getFocusedTeam().getHQ(),
                            Color.BLUE.hashCode(),
                            true
                    )));
        }

        if (team.getTeamRally() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                            "Team Rally",
                            team.getTeamRally(),
                            Color.AQUA.hashCode(),
                            true
                    )));
        }

        if (team.getHQ() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(player.getUniqueId())).forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            )));
        }

        team.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getName() + " has disbanded the team.");

        TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_DISBAND_TEAM, ImmutableMap.of(
                "playerId", player.getUniqueId(),
                "playerName", player.getName()
        ));

        team.disband();
    }
    @Subcommand("focus")
    @Description("Focus on a team.")
    public static void focus(Player player, @Name("team") Team team){
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (playerTeam == null){
            player.sendMessage(CC.translate("&7You are not a team!"));
            return;
        }

        if (playerTeam.getUniqueId() == team.getUniqueId()){
            player.sendMessage(CC.translate("&cYou can't focus your own team!"));
            return;
        }

        //Removing the current waypoint.
        if (playerTeam.getFocusedTeam() != null && playerTeam.getFocusedTeam().getHQ() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> playerTeam.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                            playerTeam.getFocusedTeam().getName() + "'s HQ",
                            playerTeam.getFocusedTeam().getHQ(),
                            Color.BLUE.hashCode(),
                            true
                    )));
        }

        playerTeam.setFocusedTeam(team);
        playerTeam.sendMessage("&d" + team.getName() + " &efaction has been focused by &d" + player.getName() + "&e.");

        // Adding the new focused team's waypoint.

        if (playerTeam.getFocusedTeam().getHQ() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> playerTeam.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().sendWaypoint(all, new LCWaypoint(
                            playerTeam.getFocusedTeam().getName() + "'s HQ",
                            playerTeam.getFocusedTeam().getHQ(),
                            Color.BLUE.hashCode(),
                            true
                    )));
        }
    }

    @Subcommand("unfocus")
    @Description("Unfocus a team.")
    public static void unfocus(Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null){
            player.sendMessage(CC.translate("&7You are not on a team!"));
            return;
        }

        if (team.getFocusedTeam() == null){
            player.sendMessage(CC.translate("&cYou aren't currently focusing anyone!"));
            return;
        }

        if (team.getFocusedTeam().getHQ() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                            team.getFocusedTeam().getName() + "'s HQ",
                            team.getFocusedTeam().getHQ(),
                            Color.BLUE.hashCode(),
                            true
                    )));
        }


        team.sendMessage("&d" + team.getFocusedTeam().getName() + " &efaction has been unfocused by &d" + player.getName() + "&e.");
        team.setFocusedTeam(null);
    }
    @Subcommand("forceinvite")
    @Description("Force a player to join your team.")
    public static void teamForceInvite(Player sender, @Name("target") OfflinePlayer player) {
        if (!Foxtrot.getInstance().getServerHandler().isForceInvitesEnabled()) {
            sender.sendMessage(ChatColor.RED + "Force-invites are not enabled on this server.");
            return;
        }

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
            sender.sendMessage(ChatColor.RED + "You don't need to use this during kit maps.");
            return;
        }

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMembers().size() >= Foxtrot.getInstance().getMapHandler().getTeamSize()) {
            sender.sendMessage(ChatColor.RED + "The max team size is " + Foxtrot.getInstance().getMapHandler().getTeamSize() + "!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " is already on your team.");
            return;
        }

        if (team.getInvitations().contains(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player has already been invited.");
            return;
        }

        if (!team.getHistoricalMembers().contains(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player has never been a member of your faction. Please use /f invite.");
            return;
        }

        /*if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not invite players while your team is raidable!");
            return;
        }*/

        if (team.getForceInvites() == 0) {
            sender.sendMessage(ChatColor.RED + "You do not have any force-invites left!");
            return;
        }

        team.setForceInvites(team.getForceInvites() - 1);
        TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_INVITE_SENT, ImmutableMap.of(
                "playerId", player,
                "invitedById", sender.getUniqueId(),
                "invitedByName", sender.getName(),
                "betrayOverride", "false",
                "usedForceInvite", "true"
        ));

        // we use a runnable so this message gets displayed at the end
        new BukkitRunnable() {
            @Override
            public void run() {
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You have used a force-invite.");

                if (team.getForceInvites() != 0) {
                    sender.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.RED + team.getForceInvites() + ChatColor.YELLOW + " of those left.");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.RED + "none" + ChatColor.YELLOW + " of those left.");
                }
            }
        }.runTask(Foxtrot.getInstance());

        team.getInvitations().add(player.getUniqueId());
        team.flagForSave();

        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player.getUniqueId());

        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " invited you to join '" + ChatColor.YELLOW + team.getName() + ChatColor.DARK_AQUA + "'.");

            FancyMessage clickToJoin =new FancyMessage("Type '").color(ChatColor.DARK_AQUA).then("/team join " + team.getName()).color(ChatColor.YELLOW);
            clickToJoin.then("' or ").color(ChatColor.DARK_AQUA);
            clickToJoin.then("click here").color(ChatColor.AQUA).command("/team join " + team.getName()).tooltip("§aJoin " + team.getName());
            clickToJoin.then(" to join.").color(ChatColor.DARK_AQUA);

            clickToJoin.send(bukkitPlayer);
        }

        team.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player.getUniqueId()) + " has been invited to the team!");
    }
    @Subcommand("forcekick")
    @Description("Forcefully kicks a member from your faction.")
    public static void teamForceKick(Player sender, OfflinePlayer player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " isn't on your team!");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
            return;
        }

        if(team.isCoLeader(player.getUniqueId()) && (!team.isOwner(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only the owner can kick other co-leaders!");
            return;
        }

        if (team.isCaptain(player.getUniqueId()) && (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
            return;
        }

        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player.getUniqueId());


        if (team.getFocusedTeam().getHQ() != null && bukkitPlayer != null){
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    team.getFocusedTeam().getName() + "'s HQ",
                    team.getFocusedTeam().getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        if (team.getTeamRally() != null && bukkitPlayer != null){
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    "Team Rally",
                    team.getTeamRally(),
                    Color.AQUA.hashCode(),
                    true
            ));
        }

        if (team.getHQ() != null && bukkitPlayer != null){
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
                "playerId", player,
                "kickedById", sender.getUniqueId(),
                "kickedByName", sender.getName(),
                "usedForceKick", "true"
        ));

        if (team.removeMember(player.getUniqueId())) {
            team.disband();
        } else {
            team.flagForSave();
        }

        Foxtrot.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);

        if (SpawnTagHandler.isTagged(bukkitPlayer)) {
            team.setDTR(team.getDTR() - 1);
            team.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " was force kicked by " + sender.getName() + " and your team lost 1 DTR!");
            long dtrCooldown;
            if (team.isRaidable()) {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of());
                dtrCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeRaidable();
            } else {
                dtrCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeDeath();
            }

            team.setDTRCooldown(dtrCooldown);
            DTRHandler.markOnDTRCooldown(team);
        } else {
            team.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " was force kicked by " + sender.getName() + "!");
        }

        if (bukkitPlayer != null) {
            //FrozenNametagHandler.reloadPlayer(bukkitPlayer);
            //FrozenNametagHandler.reloadOthersFor(bukkitPlayer);
        }
    }
    @Subcommand("forceleave")
    public static void forceLeave(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) && team.getSize() > 1) {
            sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
            return;
        }

        if (LandBoard.getInstance().getTeam(sender.getLocation()) == team) {
            sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
            return;
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null){
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    team.getFocusedTeam().getName() + "'s HQ",
                    team.getFocusedTeam().getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        if (team.getTeamRally() != null){
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    "Team Rally",
                    team.getTeamRally(),
                    Color.AQUA.hashCode(),
                    true
            ));
        }

        if (team.getHQ() != null){
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        if (team.removeMember(sender.getUniqueId())) {
            team.disband();
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");
        } else {
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            team.flagForSave();

            if (SpawnTagHandler.isTagged(sender)) {
                team.setDTR(team.getDTR() - 1);
                team.sendMessage(ChatColor.RED + sender.getName() + " forcibly left the team. Your team has lost 1 DTR.");

                sender.sendMessage(ChatColor.RED + "You have forcibly left your team. Your team lost 1 DTR.");
            } else {
                team.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the team.");

                sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
            }
        }

        //FrozenNametagHandler.reloadPlayer(sender);
        //FrozenNametagHandler.reloadOthersFor(sender);
    }
    @Subcommand("hq|home")
    @Description("Teleport to your team's HQ")
    public static void teamHQ(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.DARK_AQUA + "You are not on a team!");
            return;
        }

        if (team.getHQ() == null) {
            sender.sendMessage(ChatColor.RED + "HQ not set.");
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters during the End of the World!");
            return;
        }

        if (sender.hasMetadata("frozen")) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters while you're frozen!");
            return;
        }

        if (Foxtrot.getInstance().getInDuelPredicate().test(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a duel!");
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Use /pvp enable to toggle your PvP Timer off!");
            return;
        }

        boolean charge = team != LandBoard.getInstance().getTeam(sender.getLocation()) && !Foxtrot.getInstance().getConfig().getBoolean("legions");

        if (charge && team.getBalance() < (Foxtrot.getInstance().getServerHandler().isHardcore() ? 20 : 50)) {
            sender.sendMessage(ChatColor.RED + "Your team needs at least $" + (Foxtrot.getInstance().getServerHandler().isHardcore() ? 20 : 50) + " to teleport to your team headquarters.");
            return;
        }

        Foxtrot.getInstance().getServerHandler().beginHQWarp(sender, team, 10, charge);
    }
    @Subcommand("who|info|show|i")
    @Description("Show info about your team")
    @CommandCompletion("@team")
    public static void teamInfo(final Player sender, @Optional Team team) {

        if (team == null) {
            if (Foxtrot.getInstance().getTeamHandler().getTeam(sender) == null) {
                sender.sendMessage(CC.translate( "&7You are not on a team!"));
                return;
            }
            team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        }
        Team finalTeam = team;
        new BukkitRunnable() {
            public void run() {
                Team exactPlayerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(finalTeam.getName()));

                if (exactPlayerTeam != null && exactPlayerTeam != finalTeam) {
                    exactPlayerTeam.sendTeamInfo(sender);
                }

                finalTeam.sendTeamInfo(sender);
            }
        }.runTask(Foxtrot.getInstance());
    }

    @Subcommand("invite|inv")
    @Description("Invite a player to your team")
    public static void teamInvite(Player sender, @Flags("other") Player player) {
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


    @Subcommand("invites")
    @Description("View your team's pending invites.")
    public static void teamInvites(Player sender) {
        StringBuilder yourInvites = new StringBuilder();

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            if (team.getInvitations().contains(sender.getUniqueId())) {
                yourInvites.append(ChatColor.GRAY).append(team.getName()).append(ChatColor.YELLOW).append(", ");
            }
        }

        if (yourInvites.length() > 2) {
            yourInvites.setLength(yourInvites.length() - 2);
        } else {
            yourInvites.append(ChatColor.GRAY).append("No pending invites.");
        }

        sender.sendMessage(ChatColor.YELLOW + "Your Invites: " + yourInvites);

        Team current = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (current != null) {
            StringBuilder invitedToYourTeam = new StringBuilder();

            for (UUID invited : current.getInvitations()) {
                invitedToYourTeam.append(ChatColor.GRAY).append(UUIDUtils.name(invited)).append(ChatColor.YELLOW).append(", ");
            }

            if (invitedToYourTeam.length() > 2) {
                invitedToYourTeam.setLength(invitedToYourTeam.length() - 2);
            } else {
                invitedToYourTeam.append(ChatColor.GRAY).append("No pending invites.");
            }

            sender.sendMessage(ChatColor.YELLOW + "Invited to your Team: " + invitedToYourTeam.toString());
        }
    }

    @Subcommand("json")
    @CommandPermission("op")
    @Description("View the JSON for a team.")
    public static void teamJSON(CommandSender sender, @Optional Team team) {
        sender.sendMessage(team.toJSON().toString());
    }

    @Subcommand("kick")
    @Description("Kick a player from your team.")
    public static void teamKick(Player sender, OfflinePlayer player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " isn't on your team!");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
            return;
        }

        if(team.isCoLeader(player.getUniqueId()) && (!team.isOwner(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only the owner can kick other co-leaders!");
            return;
        }

        if (team.isCaptain(player.getUniqueId()) && !team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
            return;
        }

        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player.getUniqueId());

        if (bukkitPlayer != null && SpawnTagHandler.isTagged(bukkitPlayer)) {
            sender.sendMessage(ChatColor.RED + bukkitPlayer.getName() + " is currently combat-tagged! You can forcibly kick " + bukkitPlayer.getName() + " by using '"
                    + ChatColor.YELLOW + "/f forcekick " + bukkitPlayer.getName() + ChatColor.RED + "' which will cost your team 1 DTR.");
            return;
        }

        if (team.getFocusedTeam().getHQ() != null && bukkitPlayer != null){
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    team.getFocusedTeam().getName() + "'s HQ",
                    team.getFocusedTeam().getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        if (team.getTeamRally() != null && bukkitPlayer != null){
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    "Team Rally",
                    team.getTeamRally(),
                    Color.AQUA.hashCode(),
                    true
            ));
        }

        if (team.getHQ() != null && bukkitPlayer != null){
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " was kicked by " + sender.getName() + "!");

        TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
                "playerId", player,
                "kickedById", sender.getUniqueId(),
                "kickedByName", sender.getName(),
                "usedForceKick", "false"
        ));

        if (team.removeMember(player.getUniqueId())) {
            team.disband();
        } else {
            team.flagForSave();
        }

        Foxtrot.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);

        if (player.isOnline() && team.getHQ() != null){
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint("HQ", team.getHQ(), 0, true));
        }
    }
    @Subcommand("newleader|leader")
    @Description("Set a new team leader")
    public static void teamLeader(Player sender, @Flags("other") @Name("target")Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not on your team.");
            return;
        }

        team.sendMessage(ChatColor.DARK_AQUA + player.getName() + " has been given ownership of " + team.getName() + ".");
        team.setOwner(player.getUniqueId());
        team.addCaptain(sender.getUniqueId());
    }

    @Subcommand("leave")
    @Description("Leave your current team")
    public static void teamLeave(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) && team.getSize() > 1) {
            sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
            return;
        }

        if (LandBoard.getInstance().getTeam(sender.getLocation()) == team) {
            sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
            return;
        }

        if(SpawnTagHandler.isTagged(sender)) {
            sender.sendMessage(ChatColor.RED + "You are combat-tagged! You can only leave your faction by using '" + ChatColor.YELLOW + "/f forceleave" + ChatColor.RED + "' which will cost your team 1 DTR.");
            return;
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null){
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    team.getFocusedTeam().getName() + "'s HQ",
                    team.getFocusedTeam().getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        if (team.getTeamRally() != null){
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    "Team Rally",
                    team.getTeamRally(),
                    Color.AQUA.hashCode(),
                    true
            ));
        }

        if (team.getHQ() != null){
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        if (team.removeMember(sender.getUniqueId())) {
            team.disband();
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");
        } else {
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            team.flagForSave();
            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the team.");

            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
        }

        /*
        FrozenNametagHandler.reloadPlayer(sender);
        FrozenNametagHandler.reloadOthersFor(sender);
         */
    }

    @Subcommand("list")
    @Description("List all teams")
    public static void teamList(final Player sender, @Optional Integer page) {
        // This is sort of intensive so we run it async (cause who doesn't love async!)
        int finalPage2;
        if (page == null) {
            finalPage2 = 1;
        } else {
            finalPage2 = page;
        }

        Integer finalPage = finalPage2;
        new BukkitRunnable() {

            public void run() {
                if (finalPage < 1) {
                    sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1");
                    return;
                }

                Map<Team, Integer> teamPlayerCount = new HashMap<>();

                // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
                for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    if (player.hasMetadata("invisible")) {
                        continue;
                    }

                    Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

                    if (playerTeam != null) {
                        if (teamPlayerCount.containsKey(playerTeam)) {
                            teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                        } else {
                            teamPlayerCount.put(playerTeam, 1);
                        }
                    }
                }

                int maxPages = (teamPlayerCount.size() / 10) + 1;
                int currentPage = Math.min(finalPage, maxPages);

                LinkedHashMap<Team, Integer> sortedTeamPlayerCount = sortByValues(teamPlayerCount);

                int start = (currentPage - 1) * 10;
                int index = 0;

                sender.sendMessage(Team.GRAY_LINE);
                sender.sendMessage(ChatColor.BLUE + "Team List " +  ChatColor.GRAY + "(Page " + currentPage + "/" + maxPages + ")");

                for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
                    index++;

                    if (index < start) {
                        continue;
                    }

                    if (index > start + 10) {
                        break;
                    }

                    ComponentBuilder teamMessage = new ComponentBuilder();

                    teamMessage.append(index + ". ").color(ChatColor.GRAY.asBungee());
                    teamMessage.append(teamEntry.getKey().getName()).color(ChatColor.YELLOW.asBungee()).event(
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                    ChatColor.YELLOW + "DTR: " + teamEntry.getKey().getDTRColor() + Team.DTR_FORMAT.format(teamEntry.getKey().getDTR()) + ChatColor.YELLOW + " / " + teamEntry.getKey().getMaxDTR() + "\n" +
                                            ChatColor.GREEN + "Click to view team info").create()));
                    teamMessage.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f who " + teamEntry.getKey().getName()));

                    teamMessage.append(" (" + teamEntry.getValue() + "/" + teamEntry.getKey().getSize() + ")").color(ChatColor.GREEN.asBungee());

                    sender.spigot().sendMessage(teamMessage.create());
                }

                sender.sendMessage(ChatColor.GRAY + "You are currently on " + ChatColor.WHITE + "Page " + currentPage + "/" + maxPages + ChatColor.GRAY + ".");
                sender.sendMessage(ChatColor.GRAY + "To view other pages, use " + ChatColor.YELLOW + "/t list <page#>" + ChatColor.GRAY + ".");
                sender.sendMessage(Team.GRAY_LINE);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public static LinkedHashMap<Team, Integer> sortByValues(Map<Team, Integer> map) {
        LinkedList<Map.Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());

        list.sort((o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<Team, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (sortedHashMap);
    }

    @Subcommand("lives add|lives deposit|lives d")
    @Description("Add or deposit lives")
    public static void livesAdd(Player sender, @Name("amount") int lives) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        if( team == null ) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You need a team to use this command.");
            return;
        }

        if( lives <= 0 ) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You really think we'd fall for that?");
            return;
        }

        int currLives = Foxtrot.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());

        if( currLives < lives ) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You only have " + net.md_5.bungee.api.ChatColor.YELLOW + currLives + net.md_5.bungee.api.ChatColor.RED + " friend lives, you cannot deposit " + net.md_5.bungee.api.ChatColor.YELLOW + lives);
            return;
        }

        Foxtrot.getInstance().getFriendLivesMap().setLives(sender.getUniqueId(), currLives - lives);
        team.addLives(lives);
        sender.sendMessage(net.md_5.bungee.api.ChatColor.GREEN + "You have deposited " + net.md_5.bungee.api.ChatColor.RED + lives + net.md_5.bungee.api.ChatColor.GREEN + "  friendlives to " + net.md_5.bungee.api.ChatColor.YELLOW + team.getName() + net.md_5.bungee.api.ChatColor.GREEN + ". You now have " + net.md_5.bungee.api.ChatColor.RED + (currLives - lives) + net.md_5.bungee.api.ChatColor.GREEN + " lives and your team now has " + net.md_5.bungee.api.ChatColor.RED + team.getLives() + net.md_5.bungee.api.ChatColor.GREEN + " lives." );
    }
    @Subcommand("revive")
    @Description("Revive a player")
    public static void livesRevive(Player sender, @Flags("other") @Name("target") Player whom) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        if( team == null ) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You need a team to use this command.");
            return;
        }

        if(!team.isCoLeader(sender.getUniqueId()) && !team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Only co-leaders and owners can use this command!");
            return;
        }

        if(team.getLives() <= 0) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Your team has no lives to use.");
            return;
        }

        if(!team.isMember(whom.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "This player is not a member of your team.");
            return;
        }

        if(!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(whom.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "This player is not death banned currently.");
            return;
        }

        team.removeLives(1);
        Foxtrot.getInstance().getDeathbanMap().revive(whom.getUniqueId());
        sender.sendMessage(net.md_5.bungee.api.ChatColor.GREEN + "You have revived " + net.md_5.bungee.api.ChatColor.RED + UUIDUtils.name(whom.getUniqueId()) + net.md_5.bungee.api.ChatColor.GREEN + ".");
    }
    @Subcommand("lives")
    @Description("View your lives")
    public static void getLives(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        if( team == null ) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You need a team to use this command.");
            return;
        }

        sender.sendMessage(net.md_5.bungee.api.ChatColor.YELLOW + "Your team has " + net.md_5.bungee.api.ChatColor.RED + team.getLives() + net.md_5.bungee.api.ChatColor.YELLOW + " lives.");
        sender.sendMessage(net.md_5.bungee.api.ChatColor.YELLOW + "To deposit lives, use /t lives add <amount>");
        sender.sendMessage(net.md_5.bungee.api.ChatColor.YELLOW + "Life deposits are FINAL!");
        sender.sendMessage(net.md_5.bungee.api.ChatColor.YELLOW + "Leaders can revive members using " + net.md_5.bungee.api.ChatColor.WHITE + "/t revive <name>");
    }

    @Subcommand("map")
    @Description("View the map")
    public static void teamMap(Player sender) {
        (new VisualClaim(sender, VisualClaimType.MAP, false)).draw(false);
    }
    @Subcommand("mute")
    @Description("Mute a player")
    @CommandPermission("foxtrot.mute")
    public static void teamMute( Player sender, @Name("team") final Team team, @Name("time") int time, @Name("reason")  String reason) {
        int timeSeconds = time * 60;

        for (UUID player : team.getMembers()) {
            teamMutes.put(player, team.getName());

            Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);

            if (bukkitPlayer != null) {
                bukkitPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team has been muted for " + TimeUtils.formatIntoMMSS(timeSeconds) + " for " + reason + ".");
            }
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_CREATED, ImmutableMap.of(
                "shadowMute", "false",
                "mutedById", sender.getUniqueId(),
                "mutedByName", sender.getName(),
                "duration", time
        ));

        new BukkitRunnable() {

            public void run() {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                        "shadowMute", "false"
                ));

                Iterator<Map.Entry<UUID, String>> mutesIterator = teamMutes.entrySet().iterator();

                while (mutesIterator.hasNext()) {
                    Map.Entry<UUID, String> mute = mutesIterator.next();

                    if (mute.getValue().equalsIgnoreCase(team.getName())) {
                        mutesIterator.remove();
                    }
                }
            }

        }.runTaskLater(Foxtrot.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.YELLOW + "Muted the team " + team.getName() + ChatColor.GRAY + " for " + TimeUtils.formatIntoMMSS(timeSeconds) + " for " + reason + ".");
    }
    @Subcommand("nullleader")
    @Description("Nullify the leader of a team")
    @CommandPermission("foxtrot.nullleader")
    public static void teamNullLeader(Player sender) {
        int nullLeaders = 0;

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() == null) {
                nullLeaders++;
                sender.sendMessage(ChatColor.RED + "- " + team.getName());
            }
        }

        if (nullLeaders == 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "No null teams found.");
        } else {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + nullLeaders + " null teams found.");
        }
    }

    @Subcommand("opclaim")
    @Description("Opclaim a team")
    @CommandPermission("foxtrot.opclaim")
    public static void teamOpClaim(final Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        sender.getInventory().remove(SELECTION_WAND);

        new BukkitRunnable() {

            public void run() {
                sender.getInventory().addItem(SELECTION_WAND.clone());
            }

        }.runTaskLater(Foxtrot.getInstance(), 1L);

        new VisualClaim(sender, VisualClaimType.CREATE, true).draw(false);

        if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
            new VisualClaim(sender, VisualClaimType.MAP, true).draw(true);
        }
    }

    @Subcommand("promote")
    @Description("Promote a player in a team.")
    public static void teamPromote(Player sender, @Name("target") OfflinePlayer player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team co-leaders (and above) can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + player.getName() + " is not on your team.");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is already a leader.");
        } else if (team.isCoLeader(player.getUniqueId())) {
            if (team.isOwner(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + player.getName() + " is already a co-leader! To make them a leader, use /t leader");
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can promote new leaders.");
            }
        } else if (team.isCaptain(player.getUniqueId())) {
            if (team.isOwner(sender.getUniqueId())) {
                team.sendMessage(ChatColor.DARK_AQUA + player.getName() + " has been promoted to Co-Leader!");
                team.addCoLeader(player.getUniqueId());
                team.removeCaptain(player.getUniqueId());
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can promote new Co-Leaders.");
            }
        } else {
            team.sendMessage(ChatColor.DARK_AQUA + player.getName() + " has been promoted to Captain!");
            team.addCaptain(player.getUniqueId());
        }
    }

    @Subcommand("rally")
    @Description("Rally for your team!")
    public static void rally(Player player){
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null){
            player.sendMessage(CC.translate("&7You are not on a team!"));
            return;
        }

        if (team.getTeamRally() != null){
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                            "Team Rally",
                            team.getTeamRally(),
                            Color.ORANGE.hashCode(),
                            true
                    )));
        }

        team.setTeamRally(player.getLocation());
        team.sendMessage("&3" + player.getName() + " has updated the team's rally point!");

        Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                .forEach(all -> LunarClientAPI.getInstance().sendWaypoint(player,
                        new LCWaypoint(
                                "Team Rally",
                                team.getTeamRally(),
                                Color.ORANGE.hashCode(),
                                true
                        )));
    }

    @Subcommand("unrally")
    @Description("Unsets the rally point.")
    public static void unrally(Player player){
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null){
            player.sendMessage(CC.translate("&7You are not on a team!"));
            return;
        }

        if (team.getTeamRally() == null){
            player.sendMessage(CC.translate("&cYou don't have an active rally!"));
            return;
        }

        team.setTeamRally(null);
        team.sendMessage("&3" + player.getName() + " has removed the team's rally point!");

        Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                        "Team Rally",
                        team.getTeamRally(),
                        Color.AQUA.hashCode(),
                        true
                )));
    }

    @Subcommand("rename")
    @Description("Renames your team.")
    public static void teamRename(Player sender, @Name("newName") String newName) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (Foxtrot.getInstance().getCitadelHandler().getCappers().contains(team.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Citadel cappers cannot change their name. Please contact an admin to rename your team.");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only team owners and co-leaders can use this command!");
            return;
        }

        if (newName.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
            return;
        }

        if (newName.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
            return;
        }

        if (!ALPHA_NUMERIC.matcher(newName).find()) {
            if (Foxtrot.getInstance().getTeamHandler().getTeam(newName) == null) {
                team.rename(newName);
                sender.sendMessage(ChatColor.GREEN + "Team renamed to " + newName);
            } else {
                sender.sendMessage(ChatColor.RED + "A team with that name already exists!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
        }
    }
    @Subcommand("sethq|sethome|setheadquarters")
    @Description("Sets the team's headquarters.")
    public static void teamSetHQ(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (LandBoard.getInstance().getTeam(sender.getLocation()) != team) {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You can only set HQ in your team's territory.");
                    return;
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Setting HQ outside of your team's territory would normally be disallowed, but this check is being bypassed due to your rank.");
                }
            }

            /*
             * Removed at Jon's request.
             * https://github.com/FrozenOrb/HCTeams/issues/59
             */

//            if (sender.getLocation().getBlockY() > 100) {
//                if (!sender.isOp()) {
//                    sender.sendMessage(ChatColor.RED + "You can't set your HQ above  Y 100.");
//                    return;
//                } else {
//                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Claiming above Y 100 would normally be disallowed, but this check is being bypassed due to your rank.");
//                }
//            }


            team.setHQ(sender.getLocation());
            team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's HQ point!");

            Bukkit.getOnlinePlayers().stream().filter(player -> team.isMember(player.getUniqueId())).forEach(player -> LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint("HQ", team.getHQ(), java.awt.Color.BLUE.hashCode(), true, true)));


        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }



    @Subcommand("shadowmute")
    @Description("Mutes a player from your team.")
    @CommandPermission("foxtrot.team.shadowmute")
    public static void teamShadowMute(Player sender, final Team team, int time) {
        int timeSeconds = time * 60;

        for (UUID player : team.getMembers()) {
            teamShadowMutes.put(player, team.getName());
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_CREATED, ImmutableMap.of(
                "shadowMute", "true",
                "mutedById", sender.getUniqueId(),
                "mutedByName", sender.getName(),
                "duration", time
        ));

        new BukkitRunnable() {

            public void run() {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                        "shadowMute", "true"
                ));

                teamShadowMutes.entrySet().removeIf(mute -> mute.getValue().equalsIgnoreCase(team.getName()));
            }

        }.runTaskLater(Foxtrot.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.YELLOW + "Shadow muted the team " + team.getName() + ChatColor.GRAY + " for " + TimeUtils.formatIntoMMSS(timeSeconds) + ".");
    }


    static {
        warn.add(300);
        warn.add(270);
        warn.add(240);
        warn.add(210);
        warn.add(180);
        warn.add(150);
        warn.add(120);
        warn.add(90);
        warn.add(60);
        warn.add(30);
        warn.add(10);
        warn.add(5);
        warn.add(4);
        warn.add(3);
        warn.add(2);
        warn.add(1);

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new TeamCommands(), Foxtrot.getInstance());
    }

    @Getter private static Map<String, Long> warping = new ConcurrentHashMap<>();
    private static List<String> damaged = Lists.newArrayList();

    @Subcommand("stuck|unstuck")
    @Description("Teleports you to the nearest spawn point.")
    public static void teamStuck(final Player sender) {
        if (warping.containsKey(sender.getName())) {
            sender.sendMessage(ChatColor.RED +"You are already being warped!");
            return;
        }

        if (sender.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED +"You can only use this command from the overworld.");
            return;
        }

        int seconds = sender.isOp() && sender.getGameMode() == GameMode.CREATIVE ? 5 : 60;
        warping.put(sender.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds));

        new BukkitRunnable() {

            private int seconds = sender.isOp() && sender.getGameMode() == GameMode.CREATIVE ? 5 : 60;

            private Location loc = sender.getLocation();

            private int xStart = (int) loc.getX();
            private int yStart = (int) loc.getY();
            private int zStart = (int) loc.getZ();

            private Location nearest;

            @Override
            public void run() {
                if (damaged.contains(sender.getName())) {
                    sender.sendMessage(ChatColor.RED + "You took damage, teleportation cancelled!");
                    damaged.remove(sender.getName());
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                if (!sender.isOnline()) {
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                // Begin asynchronously searching for an available location prior to the actual teleport
                if (seconds == 5) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            nearest = nearestSafeLocation(sender.getLocation());
                        }

                    }.runTask(Foxtrot.getInstance());
                }

                Location loc = sender.getLocation();

                if (seconds <= 0) {
                    if (nearest == null) {
                        kick(sender);
                    } else {
                        Foxtrot.getInstance().getLogger().info("Moved " + sender.getName() + " " + loc.distance(nearest) + " blocks from " + toStr(loc) + " to " + toStr(nearest));

                        sender.teleport(nearest);
                        sender.sendMessage(ChatColor.YELLOW + "Teleported you to the nearest safe area!");
                    }

                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                // More than 5 blocks away
                if ((loc.getX() >= xStart + MAX_DISTANCE || loc.getX() <= xStart - MAX_DISTANCE) || (loc.getY() >= yStart + MAX_DISTANCE || loc.getY() <= yStart - MAX_DISTANCE) || (loc.getZ() >= zStart + MAX_DISTANCE || loc.getZ() <= zStart - MAX_DISTANCE)) {
                    sender.sendMessage(ChatColor.RED + "You moved more than " + MAX_DISTANCE + " blocks, teleport cancelled!");
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                /* Not necessary if we put the stuck timer in sidebar
                if (warn.contains(seconds)) {
                    sender.sendMessage(ChatColor.YELLOW + "You will be teleported in " + ChatColor.RED.toString() + ChatColor.BOLD + TimeUtils.formatIntoMMSS(seconds) + ChatColor.YELLOW + "!");
                }
                */

                seconds--;
            }

        }.runTaskTimer(Foxtrot.getInstance(), 0L, 20L);
    }

    private static String toStr(Location loc) {
        return "{x=" + loc.getBlockX() + ", y=" + loc.getBlockY() + ", z=" + loc.getBlockZ() + "}";
    }

    public static Location nearestSafeLocation(Location origin) {
        LandBoard landBoard = LandBoard.getInstance();

        if (landBoard.getClaim(origin) == null) {
            return (getActualHighestBlock(origin.getBlock()).getLocation().add(0 , 1, 0));
        }

        // Start iterating outward on both positive and negative X & Z.
        for (int xPos = 2, xNeg = -2; xPos < 250; xPos += 2, xNeg -= 2) {
            for (int zPos = 2, zNeg = -2; zPos < 250; zPos += 2, zNeg -= 2) {
                Location atPos = origin.clone().add(xPos, 0, zPos);

                // Try to find a unclaimed location with no claims adjacent
                if (landBoard.getClaim(atPos) == null && !isAdjacentClaimed(atPos)) {
                    return (getActualHighestBlock(atPos.getBlock()).getLocation().add(0, 1, 0));
                }

                Location atNeg = origin.clone().add(xNeg, 0, zNeg);

                // Try again to find a unclaimed location with no claims adjacent
                if (landBoard.getClaim(atNeg) == null && !isAdjacentClaimed(atNeg)) {
                    return (getActualHighestBlock(atNeg.getBlock()).getLocation().add(0, 1, 0));
                }
            }
        }

        return (null);
    }

    @EventHandler(priority= EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (warping.containsKey(player.getName())) {
                damaged.add(player.getName());
            }
        }
    }

    private static Block getActualHighestBlock(Block block) {
        block = block.getWorld().getHighestBlockAt(block.getLocation());

        while (block.getType() == Material.AIR && block.getY() > 0) {
            block = block.getRelative(BlockFace.DOWN);
        }

        return (block);
    }

    private static void kick(Player player){
        player.setMetadata("loggedout", new FixedMetadataValue(Foxtrot.getInstance(), true));
        player.kickPlayer(ChatColor.RED + "We couldn't find a safe location, so we safely logged you out for now. Contact a staff member before logging back on! " + ChatColor.BLUE + "TeamSpeak: ts." + Foxtrot.getInstance().getServerHandler().getNetworkWebsite());
    }

    /**
     * @param base center block
     * @return list of all adjacent locations
     */
    private static List<Location> getAdjacent(Location base) {
        List<Location> adjacent = new ArrayList<>();

        // Add all relevant locations surrounding the base block
        for(BlockFace face : BlockFace.values()) {
            if(face != BlockFace.DOWN && face != BlockFace.UP) {
                adjacent.add(base.getBlock().getRelative(face).getLocation());
            }
        }

        return adjacent;
    }

    /**
     *
     * @param location location to check for
     * @return if any of it's blockfaces are claimed
     */
    private static boolean isAdjacentClaimed(Location location) {
        for (Location adjacent : getAdjacent(location)) {
            if (LandBoard.getInstance().getClaim(adjacent) != null) {
                return true; // we found a claim on an adjacent block!
            }
        }

        return false;
    }

    @Subcommand("tpall")
    @Description("Teleports all players to the nearest safe location")
    @CommandPermission("foxtrot.tpall")
    public static void teamTP(Player sender, @Name("team") Team team) {
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public @NotNull String getPromptText(@NotNull ConversationContext context) {
                return "§aAre you sure you want to teleport all players in " + team.getName() + " ("  + team.getOnlineMembers().size() + ") to your location? Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    for (Player player : team.getOnlineMembers()) {
                        player.teleport(sender.getLocation());
                    }

                    sender.sendMessage(ChatColor.GREEN + "Teleported " + team.getOnlineMembers().size() + " to you.");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

    @Subcommand("top")
    @Description("Lists the top factions!")
    public static void teamList(final CommandSender sender) {
        // This is sort of intensive so we run it async (cause who doesn't love async!)
        new BukkitRunnable() {

            public void run() {
                LinkedHashMap<Team, Integer> sortedTeamPlayerCount = getSortedTeams();

                int index = 0;

                sender.sendMessage(Team.GRAY_LINE);

                for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {

                    if (teamEntry.getKey().getOwner() == null) {
                        continue;
                    }

                    index++;

                    if (10 <= index) {
                        break;
                    }

                    ComponentBuilder teamMessage = new ComponentBuilder();

                    Team team = teamEntry.getKey();

                    teamMessage.append(index + ". ").color(ChatColor.GRAY.asBungee());
                    teamMessage.append(teamEntry.getKey().getName()).color(sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN.asBungee() : ChatColor.RED.asBungee())
                            .event((new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder((teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN.asBungee() : ChatColor.RED.asBungee()) + teamEntry.getKey().getName() + "\n" +
                                    ChatColor.LIGHT_PURPLE + "Leader: " + ChatColor.GRAY + UUIDUtils.name(teamEntry.getKey().getOwner()) + "\n\n" +
                                    ChatColor.LIGHT_PURPLE + "Balance: " + ChatColor.GRAY + "$" + team.getBalance() + "\n" +
                                    ChatColor.LIGHT_PURPLE + "Kills: " + ChatColor.GRAY.toString() + team.getKills() + "\n" +
                                    ChatColor.LIGHT_PURPLE + "Deaths: " + ChatColor.GRAY.toString() + team.getDeaths() + "\n\n" +
                                    ChatColor.LIGHT_PURPLE + "KOTH Captures: " + ChatColor.GRAY.toString() + team.getKothCaptures() + "\n" +
                                    ChatColor.LIGHT_PURPLE + "Diamonds Mined: " + ChatColor.GRAY.toString() + team.getDiamondsMined() + "\n\n" +
                                    ChatColor.GREEN + "Click to view team info").create())));
                    teamMessage.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/t who " + teamEntry.getKey().getName()));
                    teamMessage.append(" - ").color(ChatColor.YELLOW.asBungee());
                    teamMessage.append(teamEntry.getValue().toString()).color(ChatColor.GRAY.asBungee());

                    sender.spigot().sendMessage(teamMessage.create());
                }

                sender.sendMessage(Team.GRAY_LINE);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public static LinkedHashMap<Team, Integer> getSortedTeams() {
        Map<Team, Integer> teamPointsCount = new HashMap<>();

        // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            teamPointsCount.put(team, team.getPoints());
        }

        return sortByValues2(teamPointsCount);
    }

    public static LinkedHashMap<Team, Integer> sortByValues2(Map<Team, Integer> map) {
        LinkedList<Map.Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());

        list.sort((o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<Team, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (sortedHashMap);
    }

    @Subcommand("tp")
    @Description("Teleport to a team")
    @CommandPermission("foxtrot.team.tp")
    public static void teamTPFaction(Player sender, @Name("team") Team team) {
        if (team.getHQ() != null) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
            sender.teleport(team.getHQ());
        } else if (team.getClaims().size() != 0) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s claim.");
            sender.teleport(team.getClaims().get(0).getMaximumPoint().add(0, 100, 0));
        } else {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " doesn't have a HQ or any claims.");
        }
    }

    @Subcommand("unally")
    @Description("Unally a team.")
    public static void teamUnally(Player sender, @Name("team") Team team) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(senderTeam.isOwner(sender.getUniqueId()) || senderTeam.isCoLeader(sender.getUniqueId()) || senderTeam.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!senderTeam.isAlly(team)) {
            sender.sendMessage(ChatColor.RED + "You are not allied to " + team.getName() + "!");
            return;
        }

        senderTeam.getAllies().remove(team.getUniqueId());
        team.getAllies().remove(senderTeam.getUniqueId());

        senderTeam.flagForSave();
        team.flagForSave();

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player.getUniqueId())) {
                player.sendMessage(senderTeam.getName(player) + ChatColor.YELLOW + " has dropped their alliance with your team.");
            } else if (senderTeam.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + "Your team has dropped its alliance with " + team.getName(sender) + ChatColor.YELLOW + ".");
            }

            if (team.isMember(player.getUniqueId()) || senderTeam.isMember(player.getUniqueId())) {
                //FrozenNametagHandler.reloadPlayer(sender);
                //FrozenNametagHandler.reloadOthersFor(sender);
            }
        }
    }

    @Subcommand("unclaim")
    @Description("Unclaims.")
    public static void teamUnclaim(Player sender, String all) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        if (!all.equals("all")) {
            all = "not all?";
        }
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team co-leaders can do this.");
            return;
        }

        if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not unclaim land while your faction is raidable!");
            return;
        }

        if (all.equalsIgnoreCase("all")) {
            int claims = team.getClaims().size();
            int refund = 0;

            for (Claim claim : team.getClaims()) {
                refund += Claim.getPrice(claim, team, false);

                Location minLoc = claim.getMinimumPoint();
                Location maxLoc = claim.getMaximumPoint();

                TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_UNCLAIM_LAND, ImmutableMap.of(
                        "playerId", sender.getUniqueId(),
                        "playerName", sender.getName(),
                        "refund", Claim.getPrice(claim, team, false),
                        "point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                        "point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
                ));
            }

            team.setBalance(team.getBalance() + refund);
            LandBoard.getInstance().clear(team);
            team.getClaims().clear();

            for (Subclaim subclaim : team.getSubclaims()) {
                LandBoard.getInstance().updateSubclaim(subclaim);
            }

            team.setSpawnersInClaim(0);
            team.getSubclaims().clear();
            team.setHQ(null);
            team.flagForSave();

            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed all of your team's claims. (" + ChatColor.LIGHT_PURPLE + claims + " total" + ChatColor.YELLOW + ")");
                }
            }

            return;
        }

        if (LandBoard.getInstance().getClaim(sender.getLocation()) != null && team.ownsLocation(sender.getLocation())) {
            Claim claim = LandBoard.getInstance().getClaim(sender.getLocation());
            int refund = Claim.getPrice(claim, team, false);

            team.setBalance(team.getBalance() + refund);
            team.getClaims().remove(claim);

            for (Subclaim subclaim : new ArrayList<>(team.getSubclaims())) {
                if (claim.contains(subclaim.getLoc1()) || claim.contains(subclaim.getLoc2())) {
                    team.getSubclaims().remove(subclaim);
                    LandBoard.getInstance().updateSubclaim(subclaim);
                }
            }

            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed " + ChatColor.LIGHT_PURPLE + claim.getFriendlyName() + ChatColor.YELLOW + ".");
            team.flagForSave();

            LandBoard.getInstance().setTeamAt(claim, null);

            Location minLoc = claim.getMinimumPoint();
            Location maxLoc = claim.getMaximumPoint();

            TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_UNCLAIM_LAND, ImmutableMap.of(
                    "playerId", sender.getUniqueId(),
                    "playerName", sender.getName(),
                    "refund", Claim.getPrice(claim, team, false),
                    "point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                    "point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
            ));

            if (team.getHQ() != null && claim.contains(team.getHQ())) {
                team.setHQ(null);
                sender.sendMessage(ChatColor.RED + "Your HQ was in this claim, so it has been unset.");
            }

            return;
        }

        sender.sendMessage(ChatColor.RED + "You do not own this claim.");
        sender.sendMessage(ChatColor.RED + "To unclaim all claims, type " + ChatColor.YELLOW + "/team unclaim all" + ChatColor.RED + ".");
    }

    @Subcommand("uninvite|revoke")
    @Description("Revoke an invite to your team.")
    public static void teamUninvite(final Player sender, final String allPlayer) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (allPlayer.equalsIgnoreCase("all")) {
                team.getInvitations().clear();
                sender.sendMessage(ChatColor.GRAY + "You have cleared all pending invitations.");
            } else {
                new BukkitRunnable() {

                    public void run() {
                        final UUID nameUUID = UUIDUtils.uuid(allPlayer);

                        new BukkitRunnable() {

                            public void run() {
                                if (team.getInvitations().remove(nameUUID)) {
                                    TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_INVITE_REVOKED, ImmutableMap.of(
                                            "playerId", allPlayer,
                                            "uninvitedById", sender.getUniqueId(),
                                            "uninvitedByName", sender.getName()
                                    ));

                                    team.getInvitations().remove(nameUUID);
                                    team.flagForSave();
                                    sender.sendMessage(ChatColor.GREEN + "Cancelled pending invitation for " + allPlayer + "!");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "No pending invitation for '" + allPlayer + "'!");
                                }
                            }

                        }.runTask(Foxtrot.getInstance());
                    }

                }.runTaskAsynchronously(Foxtrot.getInstance());
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }
    @Subcommand("unmute")
    @Description("Unmute a player from your team.")
    @CommandPermission("foxtrot.team.unmute")
    public static void teamUnMute(Player sender, @Name("team") Team team) {
        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                "shadowMute", "false"
        ));

        getTeamMutes().entrySet().removeIf(mute -> mute.getValue().equalsIgnoreCase(team.getName()));

        sender.sendMessage(ChatColor.GRAY + "Unmuted the team " + team.getName() + ChatColor.GRAY  + ".");
    }

    @Subcommand("unshadowmute")
    @Description("Unmute a player from your team.")
    @CommandPermission("foxtrot.team.unmute")
    public static void teamUnShadowMute(Player sender, Team team) {
        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                "shadowMute", "true"
        ));

        getTeamShadowMutes().entrySet().removeIf(mute -> mute.getValue().equalsIgnoreCase(team.getName()));

        sender.sendMessage(ChatColor.GRAY + "Un-shadowmuted the team " + team.getName() + ChatColor.GRAY  + ".");
    }

    @Subcommand("withdraw|w")
    @Description("Withdraw money from your team!")
    public static void teamWithdraw(Player sender, Float amount) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isOwner(sender.getUniqueId())) {
            if (team.getBalance() < amount) {
                sender.sendMessage(ChatColor.RED + "The team doesn't have enough money to do this!");
                return;
            }

            if (Double.isNaN(team.getBalance())) {
                sender.sendMessage(ChatColor.RED + "You cannot withdraw money because your team's balance is broken!");
                return;
            }

            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "You can't withdraw $0.0 (or less)!");
                return;
            }

            if (Float.isNaN(amount)) {
                sender.sendMessage(ChatColor.RED + "Nope.");
                return;
            }

            FrozenEconomyHandler.deposit(sender.getUniqueId(), amount);
            sender.sendMessage(ChatColor.YELLOW + "You have withdrawn " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " from the team balance!");

            TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_WITHDRAW_MONEY, ImmutableMap.of(
                    "playerId", sender.getUniqueId(),
                    "playerName", sender.getName(),
                    "amount", amount,
                    "oldBalance", team.getBalance(),
                    "newBalance", team.getBalance() - amount
            ));

            team.setBalance(team.getBalance() - amount);
            team.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + " withdrew " + ChatColor.LIGHT_PURPLE + "$" + amount + ChatColor.YELLOW + " from the team balance.");
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }



    public static final ItemStack SELECTION_WAND = new ItemStack(Material.WOODEN_HOE);

    static {
        ItemMeta meta = SELECTION_WAND.getItemMeta();

        meta.setDisplayName("§a§oClaiming Wand");
        meta.setLore(Arrays.asList(

                "",
                "§eRight/Left Click§6 Block",
                "§b- §fSelect claim's corners",
                "",
                "§eRight Click §6Air",
                "§b- §fCancel current claim",
                "",
                "§9Crouch §eLeft Click §6Block/Air",
                "§b- §fPurchase current claim"

        ));

        SELECTION_WAND.setItemMeta(meta);
    }
    @Subcommand("claim")
    @Description("Claim a area for your team!")
    public static void teamClaim(final Player sender) {
        if( Foxtrot.getInstance().getMapHandler().isKitMap() ) {
            sender.sendMessage(ChatColor.RED + "You cannot use this command on a Kit map.");
            return;
        }

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        /*
        if (Foxtrot.getInstance().getServerHandler().isWarzone(sender.getLocation())) {
            sender.sendMessage(ChatColor.RED + "You are currently in the Warzone and can't claim land here. The Warzone ends at " + ServerHandler.WARZONE_RADIUS + ".");
            return;
        }
         */

        if (team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId())) {
            sender.getInventory().remove(SELECTION_WAND);

            if (team.isRaidable()) {
                sender.sendMessage(ChatColor.RED + "You may not claim land while your faction is raidable!");
                return;
            }

            int slot = -1;

            for (int i = 0; i < 9; i++) {
                if (sender.getInventory().getItem(i) == null) {
                    slot = i;
                    break;
                }
            }

            if (slot == -1) {
                sender.sendMessage(ChatColor.RED + "You don't have space in your hotbar for the claim wand!");
                return;
            }

            int finalSlot = slot;

            new BukkitRunnable() {

                public void run() {
                    sender.getInventory().setItem(finalSlot, SELECTION_WAND.clone());
                }

            }.runTaskLater(Foxtrot.getInstance(), 1L);

            new VisualClaim(sender, VisualClaimType.CREATE, false).draw(false);

            if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
                new VisualClaim(sender, VisualClaimType.MAP, false).draw(true);
            }

            sender.sendMessage(ChatColor.GREEN + "Gave you a claim wand.");
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(SELECTION_WAND)) {
            VisualClaim visualClaim = VisualClaim.getVisualClaim(event.getPlayer().getName());

            if (visualClaim != null) {
                event.setCancelled(true);
                visualClaim.cancel();
            }

            event.getItemDrop().remove();
        } else if (event.getItemDrop().getItemStack().equals(SELECTION_WAND_SUBCLAIM)) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(SELECTION_WAND_SUBCLAIM);
        event.getPlayer().getInventory().remove(SELECTION_WAND);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        event.getPlayer().getInventory().remove(SELECTION_WAND_SUBCLAIM);
        event.getPlayer().getInventory().remove(SELECTION_WAND);
    }

    @Subcommand("subclaim addplayer|sub addplayer|subclaim grant|sub grant")
    @Description("Add a player to your subclaim.")
    public static void teamSubclaimAddPlayer(Player sender, Subclaim subclaim, @Flags("other") Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the team captains can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " is not on your team!");
            return;
        }

        if (subclaim.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "The player already has access to that subclaim!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + UUIDUtils.name(player.getUniqueId()) + ChatColor.YELLOW + " has been added to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + ".");
        subclaim.addMember(player.getUniqueId());
        team.flagForSave();
    }


    @Getter private static Map<String, Selection> selections = new HashMap<>();
    public static final ItemStack SELECTION_WAND_SUBCLAIM = new ItemStack(Material.WOODEN_SHOVEL);

    static {
        ItemMeta meta = SELECTION_WAND_SUBCLAIM.getItemMeta();

        meta.setDisplayName("§a§oSubclaim Wand");
        meta.setLore(Arrays.asList(

                "",
                "§eRight/Left Click§6 Block",
                "§b- §fSelect subclaim's corners"

        ));

        SELECTION_WAND_SUBCLAIM.setItemMeta(meta);
    }

    @Subcommand("subclaim|sub")
    @Description("Create a subclaim.")
    public static void teamSubclaim(Player sender) {
        sender.sendMessage(ChatColor.RED + "/t subclaim start - starts the subclaiming process");
        sender.sendMessage(ChatColor.RED + "/t subclaim map - toggles a visual subclaim map");
        sender.sendMessage(ChatColor.RED + "/t subclaim create <subclaim> - creates a subclaim");
        sender.sendMessage(ChatColor.RED + "/t subclaim addplayer <subclaim> <player> - adds a player to a subclaim");
        sender.sendMessage(ChatColor.RED + "/t subclaim removeplayer <subclaim> <player> - removes a player from a subclaim");
        sender.sendMessage(ChatColor.RED + "/t subclaim list - views all subclaims");
        sender.sendMessage(ChatColor.RED + "/t subclaim info <subclaim> - views info about a subclaim");
        sender.sendMessage(ChatColor.RED + "/t subclaim unclaim <subclaim> <player> - unclaims a subclaim");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getItem().getType() == SELECTION_WAND_SUBCLAIM.getType()) {
            if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().contains("Subclaim")) {
                event.setCancelled(true);

                if (team != null) {
                    Subclaim subclaim = team.getSubclaim(event.getClickedBlock().getLocation());

                    if (subclaim != null) {
                        event.getPlayer().sendMessage(ChatColor.RED + "(" + event.getClickedBlock().getX() + ", " + event.getClickedBlock().getY() + ", " + event.getClickedBlock().getZ() + ") is a part of " + subclaim.getName() + "!");
                        return;
                    }

                    if (LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation()) != team) {
                        event.getPlayer().sendMessage(ChatColor.RED + "This block is not a part of your teams' territory!");
                        return;
                    }
                }

                Selection selection = new Selection(null, null);

                if (selections.containsKey(event.getPlayer().getName())) {
                    selection = selections.get(event.getPlayer().getName());
                }

                int set;

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    set = 2;
                    selection.setLoc1(event.getClickedBlock().getLocation());
                } else {
                    set = 1;
                    selection.setLoc2(event.getClickedBlock().getLocation());
                }

                event.getPlayer().sendMessage(ChatColor.YELLOW + "Set subclaim's location " + ChatColor.LIGHT_PURPLE + set + ChatColor.YELLOW + " to " + ChatColor.GREEN + "(" + ChatColor.WHITE + event.getClickedBlock().getX() + ", " + event.getClickedBlock().getY() + ", " + event.getClickedBlock().getZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
                selections.put(event.getPlayer().getName(), selection);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(SELECTION_WAND_SUBCLAIM)) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(SELECTION_WAND_SUBCLAIM);
    }

    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        event.getPlayer().getInventory().remove(SELECTION_WAND_SUBCLAIM);
    }

    @Data
    @AllArgsConstructor
    public static class Selection {

        private Location loc1;
        private Location loc2;

        public boolean isComplete() {
            return (loc1 != null && loc2 != null);
        }

    }

    @Subcommand("subclaim create| sub create}")
    @Description("Creates a subclaim")
    public static void teamSubclaimCreate(Player sender, String subclaim) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return;
        }

        if (!StringUtils.isAlphanumeric(subclaim)) {
            sender.sendMessage(ChatColor.RED + "Subclaim names must be alphanumeric!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only team captains can create subclaims!");
            return;
        }

        if (team.getSubclaim(subclaim) != null) {
            sender.sendMessage(ChatColor.RED + "Your team already has a subclaim with that name!");
            return;
        }

        if (!getSelections().containsKey(sender.getName()) || !getSelections().get(sender.getName()).isComplete()) {
            sender.sendMessage(ChatColor.RED + "You do not have a region fully selected!");
            return;
        }

        Selection selection = getSelections().get(sender.getName());
        int x = Math.abs(selection.getLoc1().getBlockX() - selection.getLoc2().getBlockX());
        int z = Math.abs(selection.getLoc1().getBlockZ() - selection.getLoc2().getBlockZ());

        if (x < 3 || z < 3) {
            sender.sendMessage(ChatColor.RED + "Subclaims must be at least 3x3.");
            return;
        }

        for (Location loc : new CuboidRegion("Subclaim", selection.getLoc1(), selection.getLoc2())) {
            if (LandBoard.getInstance().getTeam(loc) != team) {
                sender.sendMessage(ChatColor.RED + "This subclaim would conflict with the claims of team §e" + LandBoard.getInstance().getTeam(loc).getName() + "§c!");
                return;
            }

            Subclaim subclaimAtLoc = team.getSubclaim(loc);

            if (subclaimAtLoc != null) {
                sender.sendMessage(ChatColor.RED + "This subclaim would conflict with " + ChatColor.YELLOW + subclaimAtLoc.getName() + ChatColor.RED + "!");
                return;
            }
        }

        Subclaim sc = new Subclaim(selection.getLoc1(), selection.getLoc2(), subclaim);

        team.getSubclaims().add(sc);
        LandBoard.getInstance().updateSubclaim(sc);
        team.flagForSave();

        sender.sendMessage(ChatColor.GREEN + "You have created the subclaim " + ChatColor.YELLOW + sc.getName() + ChatColor.GREEN + "!");
        sender.getInventory().remove(SELECTION_WAND_SUBCLAIM);
    }

    @Subcommand("subclaim info|sub info}")
    @Description("Shows info about a subclaim")
    public static void teamSubclaimInfo(Player sender, Subclaim subclaim) {
        sender.sendMessage(ChatColor.BLUE + subclaim.getName() + ChatColor.YELLOW + " Subclaim Info");
        sender.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.GRAY + "Pos1. " + ChatColor.WHITE + subclaim.getLoc1().getBlockX() + "," + subclaim.getLoc1().getBlockY() + "," + subclaim.getLoc1().getBlockZ() + ChatColor.GRAY + " Pos2. " + ChatColor.WHITE + subclaim.getLoc2().getBlockX() + "," + subclaim.getLoc2().getBlockY() + "," + subclaim.getLoc2().getBlockZ());
        sender.sendMessage(ChatColor.YELLOW + "Members: " + ChatColor.WHITE + subclaim.getMembers().toString().replace("[", "").replace("]", ""));
    }

    @Subcommand("subclaim list|sub list}")
    @Description("Lists all subclaims")
    public static void teamSubclaimList(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return;
        }

        StringBuilder access = new StringBuilder();
        StringBuilder other = new StringBuilder();

        for (Subclaim subclaim : team.getSubclaims()) {
            if (subclaim.isMember(sender.getUniqueId()) || team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
                access.append(subclaim.getName()).append(", ");
                continue;
            }

            other.append(subclaim).append(", ");
        }

        if (access.length() > 2) {
            access.setLength(access.length() - 2);
        }

        if (other.length() > 2) {
            other.setLength(other.length() - 2);
        }

        sender.sendMessage(ChatColor.BLUE + team.getName() + ChatColor.YELLOW + " Subclaim List");
        sender.sendMessage(ChatColor.YELLOW + "Subclaims you can access: " + ChatColor.WHITE + access.toString());
        sender.sendMessage(ChatColor.YELLOW + "Other Subclaims: " + ChatColor.WHITE + other.toString());
    }

    @Subcommand("subclaim map| sub map")
    @Description("Shows a map of all subclaims")
    public static void teamSubclaimMap(Player sender) {
        (new VisualClaim(sender, VisualClaimType.SUBCLAIM_MAP, false)).draw(false);
    }

    @Subcommand("subclaim removeplayer|sub removeplayer")
    @Description("Removes a player from a subclaim")
    public static void teamSubclaimRemovePlayer(Player sender, Subclaim subclaim, @Flags("other") Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the team captains can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " is not on your team!");
            return;
        }

        if (!subclaim.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "The player already does not have access to that subclaim!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + UUIDUtils.name(player.getUniqueId()) + ChatColor.YELLOW + " has been removed from the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + ".");
        subclaim.removeMember(player.getUniqueId());
        team.flagForSave();
    }

    @Subcommand("subclaim start|sub start")
    @Description("Starts a new subclaim")
    public static void teamSubclaimStart(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return;
        }

        if (!team.isCaptain(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        int slot = -1;

        for (int i = 0; i < 9; i++) {
            if (sender.getInventory().getItem(i) == null) {
                slot = i;
                break;
            }
        }

        if (slot == -1) {
            sender.sendMessage(ChatColor.RED + "You don't have space in your hotbar for the subclaim wand!");
            return;
        }

        if (!VisualClaim.getCurrentSubclaimMaps().containsKey(sender.getName())) {
            new VisualClaim(sender, VisualClaimType.SUBCLAIM_MAP, true).draw(true);
        }

        sender.getInventory().setItem(slot, SELECTION_WAND_SUBCLAIM.clone());
        sender.sendMessage(ChatColor.GREEN + "Gave you a subclaim wand.");
    }
    @Subcommand("subclaim unclaim|sub unclaim")
    @Description("Unclaims a subclaim")
    public static void teamSubclaimUnclaim(Player sender, Subclaim subclaim) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            team.getSubclaims().remove(subclaim);
            LandBoard.getInstance().updateSubclaim(subclaim);
            team.flagForSave();
            sender.sendMessage(ChatColor.RED + "You have unclaimed the subclaim " + ChatColor.YELLOW + subclaim.getName() + ChatColor.RED + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Only team captains can unclaim subclaims!");
        }
    }

    @Subcommand("pointbr|pbr|")
    public static void teamPointBreakDown(Player player, @Optional final Team team) {
        player.sendMessage(ChatColor.GOLD + "Point Breakdown of " + team.getName());

        for (String info : team.getPointBreakDown()) {
            player.sendMessage(info);
        }
    }

    @Default
    @HelpCommand
    @Subcommand("help")
    public static void team(Player sender) {

        String[] msg = {

                "§6§m-----------------------------------------------------",
                "§9§lTeam Help §7- §eTeam Help",
                "§7§m-----------------------------------------------------",


                "§9General Commands:",
                "§e/t create <teamName> §7- Create a new team",
                "§e/t accept <teamName> §7- Accept a pending invitation",
                "§e/t lives add <amount> §7- Irreversibly add lives to your faction",
                "§e/t leave §7- Leave your current team",
                "§e/t home §7- Teleport to your team home",
                "§e/t stuck §7- Teleport out of enemy territory",
                "§e/t deposit <amount§7|§eall> §7- Deposit money into your team balance",


                "",
                "§9Information Commands:",
                "§e/t who [player§7|§eteamName] §7- Display team information",
                "§e/t map §7- Show nearby claims (identified by pillars)",
                "§e/t list §7- Show list of teams online (sorted by most online)",


                "",
                "§9Captain Commands:",
                "§e/t invite <player> §7- Invite a player to your team",
                "§e/t uninvite <player> §7- Revoke an invitation",
                "§e/t invites §7- List all open invitations",
                "§e/t kick <player> §7- Kick a player from your team",
                "§e/t claim §7- Start a claim for your team",
                "§e/t subclaim §7- Show the subclaim help page",
                "§e/t sethome §7- Set your team's home at your current location",
                "§e/t withdraw <amount> §7- Withdraw money from your team's balance",
                "§e/t announcement [message here] §7- Set your team's announcement",

                "",
                "§9Leader Commands:",

                "§e/t coleader <add|remove> <player> §7- Add or remove a co-leader",
                "§e/t captain <add|remove> <player> §7- Add or remove a captain",
                "§e/t revive <player> §7- Revive a teammate using team lives",
                "§e/t unclaim [all] §7- Unclaim land",
                "§e/t rename <newName> §7- Rename your team",
                "§e/t disband §7- Disband your team",


                "§6§m-----------------------------------------------------",



        };
        sender.sendMessage(msg);
    }





}

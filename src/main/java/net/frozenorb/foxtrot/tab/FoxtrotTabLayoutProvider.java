package net.frozenorb.foxtrot.tab;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.nosequel.tab.shared.entry.TabElement;
import io.github.nosequel.tab.shared.entry.TabElementHandler;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.EventScheduledTime;
import net.frozenorb.foxtrot.events.koth.KOTH;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.PlayerDirection;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class FoxtrotTabLayoutProvider implements TabElementHandler {

    private LinkedHashMap<Team, Integer> cachedTeamList = Maps.newLinkedHashMap();
    long cacheLastUpdated;


    public static String formatIntoDetailedString(int secs) {
        if (secs <= 60) {
            return "1 minute";
        } else {
            int remainder = secs % 86400;
            int days = secs / 86400;
            int hours = remainder / 3600;
            int minutes = remainder / 60 - hours * 60;
            String fDays = days > 0 ? " " + days + " day" + (days > 1 ? "s" : "") : "";
            String fHours = hours > 0 ? " " + hours + " hour" + (hours > 1 ? "s" : "") : "";
            String fMinutes = minutes > 0 ? " " + minutes + " minute" + (minutes > 1 ? "s" : "") : "";
            return (fDays + fHours + fMinutes).trim();
        }

    }

    @Override
    public TabElement getElement(Player player) {
        final TabElement layout = new TabElement();
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        TabListMode mode = Foxtrot.getInstance().getTabListModeMap().getTabListMode(player.getUniqueId());

        String serverName = Foxtrot.getInstance().getServerHandler().getTabServerName();
        String titleColor = Foxtrot.getInstance().getServerHandler().getTabSectionColor();
        String infoColor = Foxtrot.getInstance().getServerHandler().getTabInfoColor();

        layout.add(1, 0, serverName);

        int y = -1;
        if (team != null) {
            layout.add(0, ++y, CC.translate(titleColor + "Home:"));

            if (team.getHQ() != null) {
                String homeLocation = infoColor.toString() + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY() + ", " + team.getHQ().getBlockZ();
                layout.add(0, ++y, CC.translate(homeLocation));
            } else {
                layout.add(0, ++y, CC.translate(infoColor + "Not Set"));
            }

            ++y; // blank

            int balance = (int) team.getBalance();
            layout.add(0, ++y, CC.translate(titleColor + "Team Info:"));
            layout.add(0, ++y, CC.translate(infoColor + "DTR: " + (team.isRaidable() ? ChatColor.DARK_RED : infoColor) + Team.DTR_FORMAT.format(team.getDTR())));
            layout.add(0, ++y, infoColor + "Online: " + team.getOnlineMemberAmount() + "/" + team.getMembers().size());
            layout.add(0, ++y, infoColor + "Balance: $" + balance);

            ++y; // blank
        }

        layout.add(0, ++y, CC.translate(titleColor + "Player Info:"));
        layout.add(0, ++y, CC.translate(infoColor + "Kills: " + Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId())));

        ++y; // blank

        layout.add(0, ++y, CC.translate(titleColor + "Your Location:"));

        String location;

        Location loc = player.getLocation();
        Team ownerTeam = LandBoard.getInstance().getTeam(loc);

        if (ownerTeam != null) {
            location = ownerTeam.getName(player.getPlayer());
        } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(loc)) {
            location = ChatColor.GRAY + "The Wilderness";
        } else if (LandBoard.getInstance().getTeam(loc) != null && LandBoard.getInstance().getTeam(loc).getName().equalsIgnoreCase("citadel")) {
            location = titleColor + "Citadel";
        } else {
            location = ChatColor.RED + "Warzone";
        }

        layout.add(0, ++y, CC.translate(location));

        //TODO Frozenorb devs addded a thing to make it not update every 1/4 a second so we should do that or update tab every 20 ticks
        //TODO Check in w/ string about 1.18/1.19 tab
        String direction = PlayerDirection.getCardinalDirection(player);
        if (direction != null) {
            layout.add(0, ++y, ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ") [" + direction + "]");
        } else {
            layout.add(0, ++y, ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockZ() + ")");
        }
        ++y; // blank

        KOTH activeKOTH = null;
        for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (!(event instanceof KOTH koth)) continue;
            if (koth.isActive() && !koth.isHidden()) {
                activeKOTH = koth;
                break;
            }
        }

        if (activeKOTH == null) {
            Date now = new Date();

            String nextKothName = null;
            Date nextKothDate = null;

            for (Map.Entry<EventScheduledTime, String> entry : Foxtrot.getInstance().getEventHandler().getEventSchedule().entrySet()) {
                if (entry.getKey().toDate().after(now)) {
                    if (nextKothDate == null || nextKothDate.getTime() > entry.getKey().toDate().getTime()) {
                        nextKothName = entry.getValue();
                        nextKothDate = entry.getKey().toDate();
                    }
                }
            }

            if (nextKothName != null) {
                layout.add(0, ++y, titleColor + "Next KOTH:");
                layout.add(0, ++y, infoColor + nextKothName);

                Event event = Foxtrot.getInstance().getEventHandler().getEvent(nextKothName);

                if (event != null && event instanceof KOTH) {
                    KOTH koth = (KOTH) event;
                    layout.add(0, ++y, CC.translate(infoColor.toString() + koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockY() + ", " + koth.getCapLocation().getBlockZ())); // location

                    int seconds = (int) ((nextKothDate.getTime() - System.currentTimeMillis()) / 1000);
                    layout.add(0, ++y, CC.translate(titleColor + "Goes active in:"));

                    String time = formatIntoDetailedString(seconds)
                            .replace("minutes", "min").replace("minute", "min")
                            .replace("seconds", "sec").replace("second", "sec");

                    layout.add(0, ++y, CC.translate(infoColor + time));
                }
            }
        } else {
            layout.add(0, ++y, CC.translate(titleColor + activeKOTH.getName()));
            layout.add(0, ++y, CC.translate(infoColor + TimeUtils.formatIntoHHMMSS(activeKOTH.getRemainingCapTime())));
            layout.add(0, ++y, CC.translate(infoColor.toString() + activeKOTH.getCapLocation().getBlockX() + ", " + activeKOTH.getCapLocation().getBlockY() + ", " + activeKOTH.getCapLocation().getBlockZ())); // location
        }

        if (team != null) {
            layout.add(1, mode == TabListMode.DETAILED_WITH_FACTION_INFO ? 5 : 2, CC.translate(titleColor + team.getName()));

            String watcherName = ChatColor.DARK_GREEN + player.getName();
            if (team.isOwner(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "**";
            } else if (team.isCoLeader(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "**";
            } else if (team.isCaptain(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "*";
            }

            layout.add(1, mode == TabListMode.DETAILED_WITH_FACTION_INFO ? 6 : 3, CC.translate(watcherName), player.getPing()); // the viewer is always first on the list

            Player owner = null;
            List<Player> coleaders = Lists.newArrayList();
            List<Player> captains = Lists.newArrayList();
            List<Player> members = Lists.newArrayList();
            for (Player member : team.getOnlineMembers()) {
                if (team.isOwner(member.getUniqueId())) {
                    owner = member;
                } else if (team.isCoLeader(member.getUniqueId())) {
                    coleaders.add(member);
                } else if (team.isCaptain(member.getUniqueId())) {
                    captains.add(member);
                } else {
                    members.add(member);
                }
            }

            int x = 1;
            y = mode == TabListMode.DETAILED ? 4 : 7;

            // then the owner
            if (owner != null && owner != player) {
                layout.add(x, y, CC.translate(ChatColor.DARK_GREEN + owner.getName() + ChatColor.GRAY + "**"), owner.getPing());

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // then the coleaders
            for (Player coleader : coleaders) {
                if (coleader == player) continue;

                layout.add(x, y, CC.translate(ChatColor.DARK_GREEN + coleader.getName() + ChatColor.GRAY + "**"), coleader.getPing());

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }


            // then the captains
            for (Player captain : captains) {
                if (captain == player) continue;

                layout.add(x, y, CC.translate(ChatColor.DARK_GREEN + captain.getName() + ChatColor.GRAY + "*"), captain.getPing());

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // and only then, normal members.
            for (Player member : members) {
                if (member == player) continue;

                layout.add(x, y, ChatColor.DARK_GREEN + member.getName(), member.getPing());

                y++;

                if (y >= 20) {
                    y = 0;
                    x++;
                }
            }

            // basically, if we're not on the third column yet, set the y to 0, and go to the third column.
            // if we're already there, just place whatever we got under the last player's name
            if (x < 2) {
                y = 0;
            } else {
                y++; // comment this out if you don't want a space in between the last player and the info below:
            }
        }

        if (team == null) {
            y = 0;
        }

        if (mode == TabListMode.DETAILED) {
            String endPortalLocation = Foxtrot.getInstance().getMapHandler().getEndPortalLocation();
            if (endPortalLocation != null && (!endPortalLocation.equals("N/A") && !endPortalLocation.isEmpty())) {
                layout.add(2, y, CC.translate(titleColor + "End Portals:"));
                layout.add(2, ++y, CC.translate(infoColor + endPortalLocation));
                layout.add(2, ++y, CC.translate(infoColor + "in each quadrant"));

                ++y;
                layout.add(2, ++y, CC.translate(titleColor + "Kit:"));
                layout.add(2, ++y, CC.translate(infoColor + Foxtrot.getInstance().getServerHandler().getEnchants()));
            } else {
                layout.add(2, y, CC.translate(titleColor + "Kit:"));
                layout.add(2, ++y, CC.translate(infoColor + Foxtrot.getInstance().getServerHandler().getEnchants()));
            }

            ++y;
            layout.add(2, ++y, CC.translate(titleColor + "Border:"));
            layout.add(2, ++y, CC.translate(infoColor + String.valueOf(BorderListener.BORDER_SIZE)));

            ++y;
            layout.add(2, ++y, CC.translate(titleColor + "Players Online:"));
            layout.add(2, ++y, CC.translate(infoColor + String.valueOf(Bukkit.getOnlinePlayers().size())));

            Set<ObjectId> cappers = Foxtrot.getInstance().getCitadelHandler().getCappers();

            if (!cappers.isEmpty()) {
                Set<String> capperNames = new HashSet<>();

                for (ObjectId capper : cappers) {
                    Team capperTeam = Foxtrot.getInstance().getTeamHandler().getTeam(capper);

                    if (capperTeam != null) {
                        capperNames.add(capperTeam.getName());
                    }
                }

                if (!capperNames.isEmpty()) {
                    ++y;
                    layout.add(2, ++y, CC.translate(titleColor + "Citadel Cappers:"));

                    for (String capper : capperNames) {
                        layout.add(2, ++y, CC.translate(infoColor + capper));
                    }
                }
            }
        } else {
            layout.add(1, 2, CC.translate(titleColor + "Players Online:"));
            layout.add(1, 3, CC.translate(infoColor + String.valueOf(Bukkit.getOnlinePlayers().size())));

            // faction list (10 entries)
            boolean shouldReloadCache = cachedTeamList == null || (System.currentTimeMillis() - cacheLastUpdated > 2000);

            y = 1;

            Map<Team, Integer> teamPlayerCount = new HashMap<>();

            if (shouldReloadCache) {
                // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
                for (Player other : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    if (other.hasMetadata("invisible")) {
                        continue;
                    }

                    Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(other);

                    if (playerTeam != null) {
                        if (teamPlayerCount.containsKey(playerTeam)) {
                            teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                        } else {
                            teamPlayerCount.put(playerTeam, 1);
                        }
                    }
                }
            }

            LinkedHashMap<Team, Integer> sortedTeamPlayerCount;

            if (shouldReloadCache) {
                sortedTeamPlayerCount = TeamListCommand.sortByValues(teamPlayerCount);
                cachedTeamList = sortedTeamPlayerCount;
                cacheLastUpdated = System.currentTimeMillis();
            } else {
                sortedTeamPlayerCount = cachedTeamList;
            }

            int index = 0;

            boolean title = false;

            for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
                index++;

                if (index > 19) {
                    break;
                }

                if (!title) {
                    title = true;
                    layout.add(2, 0, CC.translate(titleColor + "Team List:"));
                }

                String teamName = teamEntry.getKey().getName();
                String teamColor = teamEntry.getKey().isMember(player.getUniqueId()) ? ChatColor.GREEN.toString() : infoColor;

                if (teamName.length() > 10) teamName = teamName.substring(0, 10);

                layout.add(2, y++, CC.translate(teamColor + teamName + ChatColor.GRAY + " (" + teamEntry.getValue() + ")"));
            }
        }

        return layout;
    }
}

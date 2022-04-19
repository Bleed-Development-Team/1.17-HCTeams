package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Sender;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TeamListCommand {

    @Command(value={ "team list", "t list", "f list", "faction list", "fac list" })
    public static void teamList(@Sender final Player sender, @Optional("1") Integer page) {
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

}
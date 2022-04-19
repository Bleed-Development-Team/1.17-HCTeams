package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TeamTopCommand {

    @Command(value={ "team top", "t top", "f top", "faction top", "fac top" })
    public static void teamList(@Sender final CommandSender sender) {
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

        return sortByValues(teamPointsCount);
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

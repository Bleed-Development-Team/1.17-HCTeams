package net.frozenorb.foxtrot.team.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.RedisSaveTask;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.util.Date;

public class TeamDataCommands {

    @Command(value={ "exportteamdata" })
    @Permission(value = "op")
    public static void exportTeamData(@Sender CommandSender sender, @Name("file") String fileName) {
        File file = new File(fileName);

        if (file.exists()) {
            sender.sendMessage(ChatColor.RED + "An export under that name already exists.");
            return;
        }

        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

            out.writeUTF(sender.getName());
            out.writeUTF(new Date().toString());
            out.writeInt(Foxtrot.getInstance().getTeamHandler().getTeams().size());

            for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
                out.writeUTF(team.getName());
                out.writeUTF(team.saveString(false));
            }

            sender.sendMessage(ChatColor.GOLD + "Saved " + Foxtrot.getInstance().getTeamHandler().getTeams().size() + " teams to " + ChatColor.GREEN + file.getAbsolutePath() + ChatColor.GOLD + ".");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Could not import teams! Check console for errors.");
        }
    }

    @Command(value ={ "importteamdata" })
    @Permission(value = "op")
    public static void importTeamData(@Sender CommandSender sender, @Name("file") String fileName) {
        long startTime = System.currentTimeMillis();
        File file = new File(fileName);

        if (!file.exists()) {
            sender.sendMessage(ChatColor.RED + "An export under that name does not exist.");
            return;
        }

        try {
            Foxtrot.getInstance().runRedisCommand((jedis) -> {
                jedis.flushAll();
                return null;
            });

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            String author = in.readUTF();
            String created = in.readUTF();
            int teamsToRead = in.readInt();

            for (int i = 0; i < teamsToRead; i++) {
                String teamName = in.readUTF();
                String teamData = in.readUTF();

                Team team = new Team(teamName);
                team.load(teamData, true);

                Foxtrot.getInstance().getTeamHandler().setupTeam(team, true);
            }

            LandBoard.getInstance().loadFromTeams(); // to update land board shit
            Foxtrot.getInstance().getTeamHandler().recachePlayerTeams();
            RedisSaveTask.save(sender, true);
            sender.sendMessage(ChatColor.GOLD + "Loaded " + teamsToRead + " teams from an export created by " + ChatColor.GREEN + author + ChatColor.GOLD + " at " + ChatColor.GREEN + created + ChatColor.GOLD + " and recached claims in " + ChatColor.GREEN.toString() +  (System.currentTimeMillis() - startTime) + "ms.");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Could not import teams! Check console for errors.");
        }
    }

}
package net.frozenorb.foxtrot.commands.eotw;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.listener.EndListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EOTWHandler {

    public static String BORDER_PREFIX = "&7[&6&lBorder&7] ";
    public static int maxSize = 30;
    public static int starting = 1500;

    public static long currentTime;

    private BukkitTask eotwRunnable;
    private BukkitTask preEotwRunnable;

    public void commenceCountdown(int seconds){
        CustomTimerCreateCommand.customTimers.put("&4&lEOTW In", System.currentTimeMillis() + (seconds * 1000L));

        preEotwRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                commenceEOTW(true);

                cancel();
                preEotwRunnable = null;
            }
        }.runTaskLater(Foxtrot.getInstance(), (seconds - 5) * 20L);

        eotwRunnable = new BukkitRunnable(){
            @Override
            public void run() {
                commenceEOTW(false);

                cancel();
                eotwRunnable = null;

            }
        }.runTaskLater(Foxtrot.getInstance(), seconds * 20L);
    }



    public void commenceEOTW(boolean pre){

        if (pre){
            Foxtrot.getInstance().getServerHandler().setPreEOTW(!Foxtrot.getInstance().getServerHandler().isPreEOTW());

            Foxtrot.getInstance().getDeathbanMap().wipeDeathbans();

            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1F, 1F);
            }

            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[Pre-EOTW]");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW is about to commence.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED + "PvP Protection is disabled.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All players have been un-deathbanned.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.RED + "All deathbans are now permanent.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            return;
        }


        Foxtrot.getInstance().getServerHandler().setEOTW(!Foxtrot.getInstance().getServerHandler().isEOTW());

        EndListener.endActive = !Foxtrot.getInstance().getServerHandler().isEOTW();

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1F, 1F);
        }

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()){
            if (team.hasDTRBitmask(DTRBitmask.CITADEL) || team.getName().equalsIgnoreCase("Spawn") || team.getName().equalsIgnoreCase("EOTW") || team.getName().equalsIgnoreCase("Buffer") || team.hasDTRBitmask(DTRBitmask.KOTH) || team.hasDTRBitmask(DTRBitmask.ROAD)) continue;

            team.disband();
        }

        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW has commenced.");
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All SafeZones are now Deathban.");
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
    }

}

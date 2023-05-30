package net.frozenorb.foxtrot.commands.op.eotw;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.commands.op.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.server.listener.impl.EndListener;
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

    public BukkitTask eotwRunnable;
    public BukkitTask preEotwRunnable;

    public void commenceCountdown(int seconds){
        CustomTimerCreateCommand.customTimers.put("&4&lEOTW In", System.currentTimeMillis() + (seconds * 1000L));

        preEotwRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                commenceEOTW(true);

                cancel();
                preEotwRunnable = null;
            }
        }.runTaskLater(HCF.getInstance(), (seconds - 5) * 20L);

        eotwRunnable = new BukkitRunnable(){
            @Override
            public void run() {
                commenceEOTW(false);

                cancel();
                eotwRunnable = null;

            }
        }.runTaskLater(HCF.getInstance(), seconds * 20L);
    }



    public void commenceEOTW(boolean pre){

        if (pre){
            HCF.getInstance().getServerHandler().setPreEOTW(!HCF.getInstance().getServerHandler().isPreEOTW());

            HCF.getInstance().getDeathbanMap().wipeDeathbans();

            for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1F, 1F);
            }

            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[Pre-EOTW]");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + ChatColor.BOLD + "EOTW is about to commence.");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED + "PvP Protection is disabled.");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All players have been un-deathbanned.");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.RED + "All deathbans are now permanent.");
            HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            return;
        }


        HCF.getInstance().getServerHandler().setEOTW(!HCF.getInstance().getServerHandler().isEOTW());

        EndListener.endActive = !HCF.getInstance().getServerHandler().isEOTW();

        for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1F, 1F);
        }

        for (Team team : HCF.getInstance().getTeamHandler().getTeams()){
            if (team.hasDTRBitmask(DTRBitmask.CITADEL) || team.getName().equalsIgnoreCase("EOTW") || team.getName().equalsIgnoreCase("Buffer") || team.hasDTRBitmask(DTRBitmask.KOTH) || team.hasDTRBitmask(DTRBitmask.ROAD)) continue;

            team.setDTR(-0.99);
            team.setDTRCooldown(Long.MAX_VALUE);
        }

        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW has commenced.");
        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All SafeZones are now Deathban.");
        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
    }

}

package net.frozenorb.foxtrot.gameplay.actionbar;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.server.listener.impl.GoldenAppleListener;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.provider.scoreboard.utils.ScoreFunction;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.commands.team.TeamCommands;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Logout;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class ActionbarThread extends Thread {

    public ActionbarThread(){
        super("HCTeams - Actionbar Thread");
    }

    @Override
    public void run() {
        while (true){
            for (Player player : Bukkit.getOnlinePlayers()){
                String string = "";

                String spawnTagScore = getSpawnTagScore(player);
                String enderpearlScore = getEnderpearlScore(player);
                String pvpTimerScore = getPvPTimerScore(player);
                String archerMarkScore = getArcherMarkScore(player);
                String bardEffectScore = getBardEffectScore(player);
                String bardEnergyScore = getBardEnergyScore(player);
                String fstuckScore = getFStuckScore(player);
                String logoutScore = getLogoutScore(player);
                String homeScore = getHomeScore(player);
                String appleScore = getAppleScore(player);

                if (spawnTagScore != null){
                    string += "&rCombat: &4" + spawnTagScore;
                }

                if (enderpearlScore != null){
                    string += " &rEnderpearl: &e" + enderpearlScore;
                }

                if (pvpTimerScore != null){
                    string += " &rInvincibility: &a" + pvpTimerScore;
                }

                if (archerMarkScore != null){
                    string += " &rArcher Mark: &4" + archerMarkScore;
                }

                if (bardEnergyScore != null){
                    string += " &rBard Energy: &a" + bardEnergyScore;
                }

                if (fstuckScore != null){
                    string += " &rStuck: &4" + fstuckScore;
                }

                if (logoutScore != null){
                    string += " &rLogout: &4" + logoutScore;
                }

                if (homeScore != null){
                    string += " &rHome: &9" + homeScore;
                }

                if (appleScore != null){
                    string += " &rApple: &6" + appleScore;
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(CC.translate(string)));
            }

            try{
                Thread.sleep(100L);
            }
            catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }


    public String getAppleScore(Player player) {
        if (GoldenAppleListener.getCrappleCooldown().containsKey(player.getUniqueId()) && GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) >= System.currentTimeMillis()) {
            float diff = GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getHomeScore(Player player) {
        if (ServerHandler.getHomeTimer().containsKey(player.getName()) && ServerHandler.getHomeTimer().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = ServerHandler.getHomeTimer().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getFStuckScore(Player player) {
        if (TeamCommands.getWarping().containsKey(player.getName())) {
            float diff = TeamCommands.getWarping().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return null;
    }

    public String getLogoutScore(Player player) {
        Logout logout = ServerHandler.getTasks().get(player.getName());

        if (logout != null) {
            float diff = logout.getLogoutTime() - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return null;
    }

    public String getSpawnTagScore(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            float diff = SpawnTagHandler.getTag(player);

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getEnderpearlScore(Player player) {
        if (EnderpearlCooldownHandler.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getPvPTimerScore(Player player) {
        if (HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            int secondsRemaining = HCF.getInstance().getPvPTimerMap().getSecondsRemaining(player.getUniqueId());

            if (secondsRemaining >= 0) {
                return (ScoreFunction.TIME_SIMPLE.apply((float) secondsRemaining));
            }
        }

        return (null);
    }

    public String getTimerScore(Map.Entry<String, Long> timer) {
        long diff = timer.getValue() - System.currentTimeMillis();

        if (diff > 0) {
            return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
        } else {
            return (null);
        }
    }

    public String getArcherMarkScore(Player player) {
        if (ArcherClass.isMarked(player)) {
            long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getBardEffectScore(Player player) {
        if (BardClass.getLastEffectUsage().containsKey(player.getName()) && BardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = BardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getBardEnergyScore(Player player) {
        if (BardClass.getEnergy().containsKey(player.getName())) {
            float energy = BardClass.getEnergy().get(player.getName());

            if (energy > 0) {
                // No function here, as it's a "raw" value.
                return (String.valueOf(BardClass.getEnergy().get(player.getName())));
            }
        }

        return (null);
    }
}

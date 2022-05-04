package net.frozenorb.foxtrot.scoreboard;

import io.github.thatkawaiisam.assemble.AssembleAdapter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.EOTWCommand;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.EventType;
import net.frozenorb.foxtrot.events.conquest.game.ConquestGame;
import net.frozenorb.foxtrot.events.koth.KOTH;
import net.frozenorb.foxtrot.extras.sale.Sale;
import net.frozenorb.foxtrot.listener.GoldenAppleListener;
import net.frozenorb.foxtrot.map.stats.StatsEntry;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.server.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamCommands;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.Logout;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;

import java.util.*;

public class FoxtrotScoreboardProvider implements AssembleAdapter {
    @Override
    public String getTitle(Player player) {
        return CC.translate("&6&lFox &7" + StringEscapeUtils.unescapeJava("❘") + " &fHCF");
    }

    @Override
    public List<String> getLines(Player player) {
        LinkedList<String> scores = new LinkedList<>();

        if (Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            long unbannedOn = Foxtrot.getInstance().getDeathbanMap().getDeathban(player.getUniqueId());
            long left = unbannedOn - System.currentTimeMillis();
            final String time = TimeUtils.formatIntoHHMMSS((int) left / 1000);

            scores.add("&4&l&7&m--------------------");
            scores.add("&c&lDeathban&7: &c" + time);
            scores.add("&6&lLives&7: &c" + Foxtrot.getInstance().getFriendLivesMap().getLives(player.getUniqueId()));
            scores.add("&c&7&m--------------------");


            return scores;
        }

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


        if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
            StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());

            scores.add("&6&lKills&7: &f" + stats.getKills());
            scores.add("&6&lDeaths&7: &f" + stats.getDeaths());
        }


        if (spawnTagScore != null) {
            scores.add("&c&lSpawn Tag&7: &c" + spawnTagScore);
        }

        if (homeScore != null) {
            scores.add("&9&lHome§7: &c" + homeScore);
        }

        if (appleScore != null) {
            scores.add("&6&lApple&7: &c" + appleScore);
        }

        if (enderpearlScore != null) {
            scores.add("&e&lEnderpearl&7: &c" + enderpearlScore);
        }


        if (pvpTimerScore != null) {
            if (Foxtrot.getInstance().getStartingPvPTimerMap().get(player.getUniqueId())) {
                scores.add("&a&lStarting Timer&7: &c" + pvpTimerScore);
            } else {
                scores.add("&a&lPvP Timer&7: &c" + pvpTimerScore);
            }
        }

        if (Cooldown.isOnCooldown("partner", player)){
            scores.add("&d&lPartner&7: &c" + Cooldown.getCooldownString(player,"partner"));
        }

        Iterator<Map.Entry<String, Long>> iterator = CustomTimerCreateCommand.getCustomTimers().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> timer = iterator.next();
            if (timer.getValue() < System.currentTimeMillis()) {
                iterator.remove();
                continue;
            }

            if (timer.getKey().equals("&aSOTW")) {
                if (!CustomTimerCreateCommand.sotwEnabled.contains(player.getUniqueId())) {
                    scores.add(CC.translate("&aSOTW&7: &f" + getTimerScore(timer)));
                } else {
                    scores.add(CC.translate("&a&mSOTW&7&m: &f&m" + getTimerScore(timer)));
                }
            } else if (timer.getKey().equals("&4&lEOTW In")){
                scores.add(CC.translate("&4&lEOTW &4is in &4&l" + getTimerScore(timer)));
            } else {
                scores.add(CC.translate(timer.getKey() + "&7: &c" + getTimerScore(timer)));
            }
        }
        if (Foxtrot.getInstance().getSaleManager().getSales() != null) {
            for (Sale sale : Foxtrot.getInstance().getSaleManager().getSales()) {
                scores.add(CC.translate(sale.getDisplayName() + " ends in &c" + TimeUtils.formatLongIntoDetailedString(sale.getStartedAt() + sale.getLength() - System.currentTimeMillis())));
            }
        }

        for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (!event.isActive() || event.isHidden()) {
                continue;
            }

            String displayName = switch (event.getName()) {
                case "EOTW" -> ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW";
                case "Citadel" -> ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Citadel";
                default -> ChatColor.BLUE.toString() + ChatColor.BOLD + event.getName();
            };

            scores.add(displayName + "&7: &c" + ScoreFunction.TIME_SIMPLE.apply((float) ((KOTH) event).getRemainingCapTime()));

        }

        if (EOTWCommand.isFfaEnabled()) {
            long ffaEnabledAt = EOTWCommand.getFfaActiveAt();
            if (System.currentTimeMillis() < ffaEnabledAt) {
                long difference = ffaEnabledAt - System.currentTimeMillis();
                scores.add("&4&lFFA&7: &c" + ScoreFunction.TIME_SIMPLE.apply(difference / 1000F));
            }
        }


        if (archerMarkScore != null) {
            scores.add("&6&lArcher Mark&7: &c" + archerMarkScore);
        }

        if (bardEffectScore != null) {
            scores.add("&a&lBard Effect&7: &c" + bardEffectScore);
        }

        if (bardEnergyScore != null) {
            scores.add("&b&lBard Energy&7: &c" + bardEnergyScore);
        }

        if (fstuckScore != null) {
            scores.add("&4&lStuck&7: &c" + fstuckScore);
        }

        if (logoutScore != null) {
            scores.add("&4&lLogout&7: &c" + logoutScore);
        }


        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team != null && team.getFocusedTeam() != null){
            Team focusedTeam = team.getFocusedTeam();

            if (!scores.isEmpty()){
                scores.add("&3&l&7&m--------------------");
            }
            scores.add("&6&lTeam&7: &f" + focusedTeam.getName());
            if (focusedTeam.getHQ() != null){
                scores.add("&6&lHQ&7: &f" + Math.round(focusedTeam.getHQ().getX()) + "&7, &f" + Math.round(focusedTeam.getHQ().getZ()));
            }
            scores.add("&6&lDTR&7: &f" + focusedTeam.getFormattedDTR());
            if (!focusedTeam.hasDTRBitmask(DTRBitmask.KOTH) || focusedTeam.hasDTRBitmask(DTRBitmask.CITADEL) || focusedTeam.hasDTRBitmask(DTRBitmask.SAFE_ZONE)){
                scores.add("&6&lOnline&7: &f" + focusedTeam.getOnlineMembers().size());
            }
        }

        ConquestGame conquest = Foxtrot.getInstance().getConquestHandler().getGame();

        if (conquest != null) {
            if (scores.size() != 0) {
                scores.add("&c&7&m--------------------");
            }

            scores.add("&e&lConquest:");
            int displayed = 0;

            for (Map.Entry<ObjectId, Integer> entry : conquest.getTeamPoints().entrySet()) {
                Team resolved = Foxtrot.getInstance().getTeamHandler().getTeam(entry.getKey());

                if (resolved != null) {
                    scores.add("  " + resolved.getName(player) + "&7: &f" + entry.getValue());
                    displayed++;
                }

                if (displayed == 3) {
                    break;
                }
            }

            if (displayed == 0) {
                scores.add("  &7No scores yet");
            }
        }

        if (!scores.isEmpty()) {
            scores.addFirst("&a&7&m--------------------");
            scores.add("&b&7&m--------------------");
        }

        return scores;
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
                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
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
        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            int secondsRemaining = Foxtrot.getInstance().getPvPTimerMap().getSecondsRemaining(player.getUniqueId());

            if (secondsRemaining >= 0) {
                return (ScoreFunction.TIME_SIMPLE.apply((float) secondsRemaining));
            }
        }

        return (null);
    }

    public String getTimerScore(Map.Entry<String, Long> timer) {
        long diff = timer.getValue() - System.currentTimeMillis();

        if (diff > 0) {
            return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
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
                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
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

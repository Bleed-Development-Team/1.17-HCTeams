package net.frozenorb.foxtrot.provider.scoreboard;

import io.github.thatkawaiisam.assemble.AssembleAdapter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.commands.op.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.op.eotw.commands.EOTWCommand;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.conquest.game.ConquestGame;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.mad.game.MadGame;
import net.frozenorb.foxtrot.server.listener.impl.GoldenAppleListener;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamCommands;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Logout;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class HCFScoreboardProvider implements AssembleAdapter {
    @Override
    public String getTitle(Player player) {
        return CC.translate("&b&lFrozen &7| &fHCF");
    }

    @Override
    public List<String> getLines(Player player) {
        LinkedList<String> scores = new LinkedList<>();

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

        /*
        Team playerTeam = (Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null ?
                null : Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()));

        Team team = LandBoard.getInstance().getTeam(player.getLocation());
        if (team != null) {
            if (playerTeam != null && team == playerTeam){
                scores.add(CC.translate("&6&lClaim&7: &a" + team.getName(player)));
            } else {
                scores.add(CC.translate("&6&lClaim&7: &c" + team.getName(player)));
            }
        } else {
            if (Foxtrot.getInstance().getServerHandler().isWarzone(player.getLocation())) {
                scores.add("&6&lClaim&7: &cWarzone");
            } else {
                scores.add("&6&lClaim&7: &7Wilderness");
            }
        }

         */

        /*
        if (LandBoard.getInstance().getTeam(player.getLocation()).getName() == "Spawn") {
            StatsEntry stats = HCF.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());

            scores.add("&b&lKills&7: &f" + stats.getKills());
            scores.add("&b&lDeaths&7: &f" + stats.getDeaths());
            scores.add("&2&lGems&7: &f" + HCF.getInstance().getGemsMap().getGems(player.getUniqueId()));
        }
         */


        if (spawnTagScore != null) {
            scores.add("&fCombat: &4" + spawnTagScore);
        }

        if (homeScore != null) {
            scores.add("&fHome: &9" + homeScore);
        }

        if (appleScore != null) {
            scores.add("&fApple: &6" + appleScore);
        }

        if (enderpearlScore != null) {
            scores.add("&fEnderpearl: &e" + enderpearlScore);
        }


        if (pvpTimerScore != null) {
            if (HCF.getInstance().getStartingPvPTimerMap().get(player.getUniqueId())) {
                scores.add("&fInvincibility: &a" + pvpTimerScore);
            } else {
                scores.add("&fInvincibility: &a" + pvpTimerScore);
            }
        }

        /*
        if (Cooldown.isOnCooldown("partner", player)){
            scores.add("&fPartner: &3" + Cooldown.getCooldownString(player,"partner"));
        }

         */

        Iterator<Map.Entry<String, Long>> iterator = CustomTimerCreateCommand.getCustomTimers().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> timer = iterator.next();
            if (timer.getValue() < System.currentTimeMillis()) {
                iterator.remove();
                continue;
            }

            if (timer.getKey().equals("&aSOTW")) {
                if (!CustomTimerCreateCommand.sotwEnabled.contains(player.getUniqueId())) {
                    scores.add(CC.translate("&a&lSOTW ends in " + getTimerScore(timer)));
                } else {
                    scores.add(CC.translate("&a&l&nSOTW ends in " + getTimerScore(timer)));
                }
            } else if (timer.getKey().equals("&4&lEOTW In")){
                scores.add(CC.translate("&4&lEOTW &4is in &4&l" + getTimerScore(timer)));
            } else {
                scores.add(CC.translate(timer.getKey() + "&7: &c" + getTimerScore(timer)));
            }
        }

        for (Event event : HCF.getInstance().getEventHandler().getEvents()) {
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

        MadGame madGame = HCF.getInstance().getMadHandler().getGame();

        if (madGame != null) {
            if (scores.size() != 0) {
                scores.add("&c&7&m--------------------");
            }

            scores.add("&c&lMad&7:");
            int displayed = 0;

            for (Map.Entry<ObjectId, Integer> entry : madGame.getTeamPoints().entrySet()) {
                Team resolved = HCF.getInstance().getTeamHandler().getTeam(entry.getKey());

                if (resolved != null) {
                    scores.add("&c» &r" + resolved.getName(player) + "&7: &f" + entry.getValue());
                    displayed++;
                }

                if (displayed == 3) {
                    break;
                }
            }

            if (displayed == 0) {
                scores.add(" &7No scores yet");
            }
        }

        if (EOTWCommand.isFfaEnabled()) {
            long ffaEnabledAt = EOTWCommand.getFfaActiveAt();
            if (System.currentTimeMillis() < ffaEnabledAt) {
                long difference = ffaEnabledAt - System.currentTimeMillis();
                scores.add("&fFFA: &4" + ScoreFunction.TIME_SIMPLE.apply(difference / 1000F));
            }
        }


        if (archerMarkScore != null) {
            scores.add("&fArcher Mark: &4" + archerMarkScore);
        }

        if (bardEnergyScore != null) {
            scores.add("&fBard Energy: &a" + bardEnergyScore);
        }

        if (fstuckScore != null) {
            scores.add("&fStuck: &c" + fstuckScore);
        }

        if (logoutScore != null) {
            scores.add("&fLogout: &c" + logoutScore);
        }


        Team teamB = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (teamB != null && teamB.getFocusedTeam() != null){
            Team focusedTeam = teamB.getFocusedTeam();

            if (!scores.isEmpty()){
                scores.add("&3&l&7&m--------------------");
            }
            scores.add("&b&lTeam&r&7: &f" + focusedTeam.getName());
            if (focusedTeam.getHQ() != null){
                scores.add("&b&lHQ&r&7: &f" + Math.round(focusedTeam.getHQ().getX()) + "&7, &f" + Math.round(focusedTeam.getHQ().getZ()));
            }
            if (!focusedTeam.hasDTRBitmask(DTRBitmask.KOTH) || focusedTeam.hasDTRBitmask(DTRBitmask.CITADEL) || focusedTeam.hasDTRBitmask(DTRBitmask.SAFE_ZONE)){
                scores.add("&b&lDTR&r&7: &f" + focusedTeam.getFormattedDTR());
                scores.add("&b&lOnline&r&7: &f" + focusedTeam.getOnlineMembers().size());
            }
        }



        ConquestGame conquest = HCF.getInstance().getConquestHandler().getGame();

        if (conquest != null) {
            if (scores.size() != 0) {
                scores.add("&c&7&m--------------------");
            }

            scores.add("&e&lConquest:");
            int displayed = 0;

            for (Map.Entry<ObjectId, Integer> entry : conquest.getTeamPoints().entrySet()) {
                Team resolved = HCF.getInstance().getTeamHandler().getTeam(entry.getKey());

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

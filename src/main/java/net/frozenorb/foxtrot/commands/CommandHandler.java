package net.frozenorb.foxtrot.commands;

import co.aikar.commands.PaperCommandManager;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.chat.trivia.command.TriviaCommand;
import net.frozenorb.foxtrot.commands.gameplay.*;
import net.frozenorb.foxtrot.commands.gameplay.help.HelpCommand;
import net.frozenorb.foxtrot.commands.op.*;
import net.frozenorb.foxtrot.commands.op.eotw.commands.EOTWCommand;
import net.frozenorb.foxtrot.commands.op.eotw.commands.PreEOTWCommand;
import net.frozenorb.foxtrot.commands.op.gems.GemsCommand;
import net.frozenorb.foxtrot.commands.staff.LastInvCommand;
import net.frozenorb.foxtrot.commands.staff.ReviveCommand;
import net.frozenorb.foxtrot.crates.commands.CrateCommand;
import net.frozenorb.foxtrot.crates.monthly.commands.MonthlyCrateCommands;
import net.frozenorb.foxtrot.gameplay.ability.partnerpackages.command.PartnerPackageCommand;
import net.frozenorb.foxtrot.gameplay.airdrops.command.AirDropCommand;
import net.frozenorb.foxtrot.gameplay.archerupgrades.command.ArcherUpgradesCommand;
import net.frozenorb.foxtrot.gameplay.clickable.command.ClickableItemCommand;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventParameterType;
import net.frozenorb.foxtrot.gameplay.events.citadel.commands.CitadelCommand;
import net.frozenorb.foxtrot.gameplay.events.conquest.commands.conquest.ConquestCommand;
import net.frozenorb.foxtrot.gameplay.events.conquest.commands.conquestadmin.ConquestAdminCommands;
import net.frozenorb.foxtrot.gameplay.events.koth.commands.koth.KOTHCommand;
import net.frozenorb.foxtrot.gameplay.events.koth.commands.kothschedule.KothScheduleCommands;
import net.frozenorb.foxtrot.gameplay.events.koth.summoner.command.KOTHSummonerCommand;
import net.frozenorb.foxtrot.gameplay.events.mad.commands.MadCommand;
import net.frozenorb.foxtrot.gameplay.events.region.carepackage.CarePackageHandler;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.commands.CavernCommand;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.commands.GlowCommand;
import net.frozenorb.foxtrot.gameplay.ability.commands.AbilityCommand;
import net.frozenorb.foxtrot.gameplay.ability.commands.AbiltiesCommand;
import net.frozenorb.foxtrot.gameplay.armorclass.command.ArmorClassCommand;
import net.frozenorb.foxtrot.gameplay.blockshop.command.BlockShopCommand;
import net.frozenorb.foxtrot.gameplay.guide.GuideCommand;
import net.frozenorb.foxtrot.map.stats.command.*;
import net.frozenorb.foxtrot.server.commands.ReloadCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamType;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.commands.*;
import net.frozenorb.foxtrot.team.commands.pvp.PvPCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamChatCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamChatThree;
import net.frozenorb.foxtrot.team.commands.team.TeamChatTwo;
import net.frozenorb.foxtrot.team.commands.team.TeamCommands;
import net.frozenorb.foxtrot.team.commands.team.chatspy.TeamChatSpyCommand;
import net.frozenorb.foxtrot.team.commands.team.management.TeamManagementCommands;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRBitmaskType;
import net.frozenorb.foxtrot.util.providers.SubclaimProvider;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    public CommandHandler(HCF plugin){
        PaperCommandManager m = new PaperCommandManager(plugin);
        m.getCommandContexts().registerContext(Team.class, new TeamType());
        m.getCommandContexts().registerContext(DTRBitmask.class, new DTRBitmaskType());
        m.getCommandContexts().registerContext(Event.class, new EventParameterType());
        m.getCommandContexts().registerContext(StatsTopCommand.StatsObjective.class, new StatsTopCommand.StatsObjectiveProvider());
        m.getCommandContexts().registerContext(Subclaim.class, new SubclaimProvider());

        m.registerCommand(new AddBalanceCommand());
        m.registerCommand(new BalanceCommand());
        m.registerCommand(new BitmaskCommand());
        m.registerCommand(new CobbleCommand());
        m.registerCommand(new CrowbarCommand());
        m.registerCommand(new CustomTimerCreateCommand());
        m.registerCommand(new EnderpearlCommands());
        m.registerCommand(new EOTWCommand());
        m.registerCommand(new FDToggleCommand());
        m.registerCommand(new GoppleCommand());
        m.registerCommand(new HelpCommand());
        m.registerCommand(new KOTHRewardKeyCommand());
        m.registerCommand(new LastInvCommand());
        m.registerCommand(new LocationCommand());
        m.registerCommand(new AirDropCommand());
        m.registerCommand(new LogoutCommand());
        m.registerCommand(new ArmorClassCommand());
        m.registerCommand(new LettingInCommand());
        m.registerCommand(new OresCommand());
        m.registerCommand(new PayCommand());
        m.registerCommand(new PartnerPackageCommand());
        m.registerCommand(new PlaytimeCommand());
        m.registerCommand(new PreEOTWCommand());
        m.registerCommand(new RegenCommand());
        m.registerCommand(new ReviveCommand());
        m.registerCommand(new SetBalCommand());
        m.registerCommand(new TeamManagementCommands());
        m.registerCommand(new RenameCommand());
        m.registerCommand(new GlowCommand());
        m.registerCommand(new CavernCommand());
        m.registerCommand(new CarePackageHandler());
        m.registerCommand(new SOTWCommand());
        m.registerCommand(new SpawnCommand());
        m.registerCommand(new SpawnDragonCommand());
        m.registerCommand(new ReloadCommand());
        m.registerCommand(new ClickableItemCommand());
        m.registerCommand(new TriviaCommand());
        m.registerCommand(new TellLocationCommand());
        m.registerCommand(new ToggleChatCommand());
        m.registerCommand(new ToggleDeathMessagesCommand());
        m.registerCommand(new CrateCommand());
        m.registerCommand(new CitadelCommand());
        m.registerCommand(new ConquestCommand());
        m.registerCommand(new ConquestAdminCommands());
        m.registerCommand(new KOTHCommand());
        m.registerCommand(new KothScheduleCommands());
        m.registerCommand(new BlockShopCommand());
        m.registerCommand(new ChestCommand());
        m.registerCommand(new ClearAllStatsCommand());
        m.registerCommand(new ClearLeaderboardsCommand());
        m.registerCommand(new KOTHSummonerCommand());
        m.registerCommand(new KillstreaksCommand());
        m.registerCommand(new GemsCommand());
        m.registerCommand(new LeaderboardAddCommand());
        m.registerCommand(new StatModifyCommands());
        m.registerCommand(new StatsCommand());
        m.registerCommand(new ReclaimCommand());
        m.registerCommand(new StatsTopCommand());
        m.registerCommand(new FocusCommand());
        m.registerCommand(new ForceDisbandAllCommand());
        m.registerCommand(new ForceDisbandCommand());
        m.registerCommand(new ForceJoinCommand());
        m.registerCommand(new ForceLeaveCommand());
        m.registerCommand(new ForceKickCommand());
        m.registerCommand(new ForceLeaderCommand());
        m.registerCommand(new FreezeRostersCommand());
        m.registerCommand(new ImportTeamDataCommand());
        m.registerCommand(new PowerFactionCommand());
        m.registerCommand(new ArcherUpgradesCommand());
        m.registerCommand(new MadCommand());
        m.registerCommand(new RecalculatePointsCommand());
        m.registerCommand(new ResetForceInvitesCommand());
        m.registerCommand(new SetTeamBalanceCommand());
        m.registerCommand(new StartDTRRegenCommand());
        m.registerCommand(new TeamDataCommands());
        m.registerCommand(new PvPCommand());
        m.registerCommand(new TeamChatCommand());
        m.registerCommand(new TeamChatThree());
        m.registerCommand(new TeamChatTwo());
        m.registerCommand(new TeamCommands());
        m.registerCommand(new GuideCommand());
        m.registerCommand(new TeamChatSpyCommand());
        m.registerCommand(new AbilityCommand());
        m.registerCommand(new AbiltiesCommand());
        m.registerCommand(new MonthlyCrateCommands());

        m.getCommandCompletions().registerCompletion("bitmask", c -> {
            List<String> bitmasks = new ArrayList<>();

            for (DTRBitmask bitmask : DTRBitmask.values()) {
                bitmasks.add(bitmask.getName());
            }

            return bitmasks;
        });

        m.getCommandCompletions().registerCompletion("team", c -> {
            List<String> teams = new ArrayList<>();
            for (Team team : plugin.getTeamHandler().getTeams()) {
                if (team.hasDTRBitmask(DTRBitmask.KOTH) || team.hasDTRBitmask(DTRBitmask.SAFE_ZONE) || team.hasDTRBitmask(DTRBitmask.CITADEL) || team.hasDTRBitmask(DTRBitmask.ROAD)) continue;
                teams.add(team.getName());
            }

            return teams;
        });

        m.getCommandCompletions().registerCompletion("event", c -> {
            List<String> events = new ArrayList<>();
            for (Event event : plugin.getEventHandler().getEvents()) {
                events.add(event.getName());
            }
            return events;
        });

    }

}

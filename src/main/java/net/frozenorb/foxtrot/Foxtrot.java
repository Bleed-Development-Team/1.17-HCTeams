package net.frozenorb.foxtrot;

import co.aikar.commands.PaperCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import io.github.nosequel.menu.MenuHandler;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.chat.ChatHandler;
import net.frozenorb.foxtrot.chat.chatgames.ChatGamesHandler;
import net.frozenorb.foxtrot.commands.*;
import net.frozenorb.foxtrot.crates.CrateHandler;
import net.frozenorb.foxtrot.crates.commands.CrateCommand;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.EventHandler;
import net.frozenorb.foxtrot.events.EventParameterType;
import net.frozenorb.foxtrot.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.events.citadel.commands.CitadelCommand;
import net.frozenorb.foxtrot.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.events.conquest.commands.conquest.ConquestCommand;
import net.frozenorb.foxtrot.events.conquest.commands.conquestadmin.ConquestAdminCommands;
import net.frozenorb.foxtrot.events.koth.commands.koth.KOTHCommand;
import net.frozenorb.foxtrot.events.koth.commands.kothschedule.KothScheduleCommands;
import net.frozenorb.foxtrot.events.region.carepackage.CarePackageHandler;
import net.frozenorb.foxtrot.events.region.cavern.CavernHandler;
import net.frozenorb.foxtrot.events.region.cavern.commands.CavernCommand;
import net.frozenorb.foxtrot.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.events.region.glowmtn.commands.GlowCommand;
import net.frozenorb.foxtrot.extras.ability.AbilityHandler;
import net.frozenorb.foxtrot.extras.ability.commands.AbilityCommand;
import net.frozenorb.foxtrot.extras.ability.commands.AbiltiesCommand;
import net.frozenorb.foxtrot.extras.ability.packages.AbilityPackage;
import net.frozenorb.foxtrot.extras.ability.packages.AbilityPackageHandler;
import net.frozenorb.foxtrot.extras.ability.packages.commands.PartnerPackageCommand;
import net.frozenorb.foxtrot.extras.blockshop.command.BlockShopCommand;
import net.frozenorb.foxtrot.extras.guide.GuideCommand;
import net.frozenorb.foxtrot.extras.lunar.LunarClientHandler;
import net.frozenorb.foxtrot.extras.lunar.nametag.ClientNametagProvider;
import net.frozenorb.foxtrot.extras.quests.QuestsCommand;
import net.frozenorb.foxtrot.extras.resoucepack.ResourcePack;
import net.frozenorb.foxtrot.listener.*;
import net.frozenorb.foxtrot.map.MapHandler;
import net.frozenorb.foxtrot.map.stats.command.*;
import net.frozenorb.foxtrot.packetborder.PacketBorderThread;
import net.frozenorb.foxtrot.persist.RedisSaveTask;
import net.frozenorb.foxtrot.persist.maps.*;
import net.frozenorb.foxtrot.persist.maps.statistics.*;
import net.frozenorb.foxtrot.protocol.ClientCommandPacketAdaper;
import net.frozenorb.foxtrot.protocol.SignGUIPacketAdaper;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.redis.RedisCommand;
import net.frozenorb.foxtrot.scoreboard.FoxtrotScoreboardProvider;
import net.frozenorb.foxtrot.server.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.rank.TopRankTask;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.team.TeamType;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.commands.*;
import net.frozenorb.foxtrot.team.commands.pvp.PvPCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamChatCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamChatThree;
import net.frozenorb.foxtrot.team.commands.team.TeamChatTwo;
import net.frozenorb.foxtrot.team.commands.team.TeamCommands;
import net.frozenorb.foxtrot.team.commands.team.chatspy.TeamChatSpyCommand;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRBitmaskType;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.HourEvent;
import net.frozenorb.foxtrot.util.RegenUtils;
import net.frozenorb.foxtrot.util.providers.SubclaimProvider;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Foxtrot extends JavaPlugin {

	public static String MONGO_DB_NAME = "HCTeams";

	@Getter private static Foxtrot instance;

	@Getter private MongoClient mongoPool;

	@Getter private ChatHandler chatHandler;
	@Getter private PvPClassHandler pvpClassHandler;
	@Getter private TeamHandler teamHandler;
	@Getter private ServerHandler serverHandler;
	@Getter private MapHandler mapHandler;
	@Getter private CitadelHandler citadelHandler;
	@Getter private EventHandler eventHandler;
	@Getter private ConquestHandler conquestHandler;
	@Getter private CavernHandler cavernHandler;
	@Getter private AbilityPackage abilityPackage;
	@Getter private GlowHandler glowHandler;
	@Getter private CrateHandler crateHandler;
	@Getter private AbilityHandler abilityHandler;

	@Getter private PlaytimeMap playtimeMap;
	@Getter private OppleMap oppleMap;
	@Getter private DeathbanMap deathbanMap;
	@Getter private PvPTimerMap PvPTimerMap;
	@Getter private StartingPvPTimerMap startingPvPTimerMap;
	@Getter private DeathsMap deathsMap;
	@Getter private KillsMap killsMap;
	@Getter private ChatModeMap chatModeMap;
	@Getter private FishingKitMap fishingKitMap;
	@Getter private ToggleGlobalChatMap toggleGlobalChatMap;
	@Getter private ChatSpyMap chatSpyMap;
	@Getter private DiamondMinedMap diamondMinedMap;
	@Getter private GoldMinedMap goldMinedMap;
	@Getter private IronMinedMap ironMinedMap;
	@Getter private CoalMinedMap coalMinedMap;
	@Getter private RedstoneMinedMap redstoneMinedMap;
	@Getter private LapisMinedMap lapisMinedMap;
	@Getter private EmeraldMinedMap emeraldMinedMap;
	@Getter private FirstJoinMap firstJoinMap;
	@Getter private LastJoinMap lastJoinMap;
	@Getter private SoulboundLivesMap soulboundLivesMap;
	@Getter private FriendLivesMap friendLivesMap;
	@Getter private BaseStatisticMap enderPearlsUsedMap;
	@Getter private BaseStatisticMap expCollectedMap;
	@Getter private BaseStatisticMap itemsRepairedMap;
	@Getter private BaseStatisticMap splashPotionsBrewedMap;
	@Getter private BaseStatisticMap splashPotionsUsedMap;
	@Getter private WrappedBalanceMap wrappedBalanceMap;
	@Getter private ToggleFoundDiamondsMap toggleFoundDiamondsMap;
	@Getter private ToggleDeathMessageMap toggleDeathMessageMap;
	@Getter private TabListModeMap tabListModeMap;
	@Getter private IPMap ipMap;
	@Getter private WhitelistedIPMap whitelistedIPMap;
	@Getter private CobblePickupMap cobblePickupMap;
	@Getter private KDRMap kdrMap;
	@Getter private KitmapTokensMap tokensMap;
	@Getter private RaidableTeamsMap raidableTeamsMap;

	@Getter private ChatGamesHandler chatGamesHandler;
	@Getter private CombatLoggerListener combatLoggerListener;
	@Getter
	@Setter
	// for the case of some commands in the plugin,
	// a player shouldn't be able to do them in a duel
	// thus this predicate exists to test that to avoid dep. issues
	private Predicate<Player> inDuelPredicate = (player) -> false;

	public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().serializeNulls().create();

	public static final Gson PLAIN_GSON = (new GsonBuilder()).serializeNulls().create();

	private JedisPool localJedisPool;

	private JedisPool backboneJedisPool;

	public static final Random RANDOM = new Random();

	@Override
	public void onEnable() {
		if (Bukkit.getServer().getName().contains(" ")) {
			System.out.println("*********************************************");
			System.out.println("               ATTENTION");
			System.out.println("SET server-name VALUE IN server.properties TO");
			System.out.println("A PROPER SERVER NAME. THIS WILL BE USED AS THE");
			System.out.println("MONGO DATABASE NAME.");
			System.out.println("*********************************************");
			this.getServer().shutdown();
			return;
		}

		instance = this;
		saveDefaultConfig();

		try {
			mongoPool = new MongoClient(new MongoClientURI(getConfig().getString("Mongo.URI")));
			MONGO_DB_NAME = Bukkit.getServer().getName();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			this.localJedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1");
			System.out.println("Connected to the local Jedis pool.");

		} catch (Exception e) {
			this.localJedisPool = null;
			e.printStackTrace();
		}

		try {
			this.backboneJedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1");
			System.out.println("Connected to the Backbone Jedis pool.");
		} catch (Exception e) {
			this.backboneJedisPool = null;
			e.printStackTrace();
		}

		(new DTRHandler()).runTaskTimer(this, 20L, 1200L);
		(new RedisSaveTask()).runTaskTimerAsynchronously(this, 1200L, 1200L);
		(new PacketBorderThread()).start();


		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ClientNametagProvider(), 0L, 40L);
		//Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Webhook.sendKothSchedule(), 0, 72000L);

		setupHandlers();
		setupPersistence();
		setupListeners();

		new MenuHandler(this);


		ProtocolLibrary.getProtocolManager().addPacketListener(new SignGUIPacketAdaper());
		ProtocolLibrary.getProtocolManager().addPacketListener(new ClientCommandPacketAdaper());

		Assemble assemble = new Assemble(this, new FoxtrotScoreboardProvider());

		// Set Interval (Tip: 20 ticks = 1 second).
		assemble.setTicks(2);

		// Set Style (Tip: Viper Style starts at -1 and goes down).
		assemble.setAssembleStyle(AssembleStyle.KOHI);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TopRankTask(), 0L, 20L * 60L * 5);


		for (World world : Bukkit.getWorlds()) {
			world.setThundering(false);
			world.setStorm(false);
			world.setWeatherDuration(Integer.MAX_VALUE);
			world.setGameRule(GameRule.DO_FIRE_TICK, false);
			world.setGameRule(GameRule.MOB_GRIEFING, false);
		}
		Cooldown.createCooldown("lff");
		PaperCommandManager m = new PaperCommandManager(this);
		m.getCommandContexts().registerContext(Team.class, new TeamType());
		m.getCommandContexts().registerContext(DTRBitmask.class, new DTRBitmaskType());
		m.getCommandContexts().registerContext(Event.class, new EventParameterType());
		m.getCommandContexts().registerContext(StatsTopCommand.StatsObjective.class, new StatsTopCommand.StatsObjectiveProvider());
		m.getCommandContexts().registerContext(Subclaim.class, new SubclaimProvider());
		m.getCommandCompletions().registerCompletion("team", c -> {
			List<String> teams = new ArrayList<>();
			for (Team team : getTeamHandler().getTeams()) {
				if (team.hasDTRBitmask(DTRBitmask.KOTH) || team.hasDTRBitmask(DTRBitmask.SAFE_ZONE) || team.hasDTRBitmask(DTRBitmask.CITADEL) || team.hasDTRBitmask(DTRBitmask.ROAD)) continue;
				teams.add(team.getName());
			}

			return teams;
		});
		m.getCommandCompletions().registerCompletion("event", c -> {
			List<String> events = new ArrayList<>();
			for (Event event : getEventHandler().getEvents()) {
				events.add(event.getName());
			}
			return events;
			});

		m.registerCommand(new AddBalanceCommand());
		m.registerCommand(new AssociateAccountsCommand());
		m.registerCommand(new AssociateViewCommand());
		m.registerCommand(new BalanceCommand());
		m.registerCommand(new BitmaskCommand());
		m.registerCommand(new BottleCommand());
		m.registerCommand(new CobbleCommand());
		m.registerCommand(new CrowbarCommand());
		m.registerCommand(new CSVExportCommand());
		m.registerCommand(new CustomTimerCreateCommand());
		m.registerCommand(new EcoCheckCommand());
		m.registerCommand(new EnderpearlCommands());
		m.registerCommand(new EOTWCommand());
		m.registerCommand(new FDToggleCommand());
		m.registerCommand(new GoppleCommand());
		m.registerCommand(new HelpCommand());
		m.registerCommand(new KOTHRewardKeyCommand());
		m.registerCommand(new LastInvCommand());
		m.registerCommand(new LivesCommand());
		m.registerCommand(new LocationCommand());
		m.registerCommand(new LogoutCommand());
		m.registerCommand(new OresCommand());
		m.registerCommand(new PayCommand());
		m.registerCommand(new PlaytimeCommand());
		m.registerCommand(new PreEOTWCommand());
		m.registerCommand(new RecachePlayerTeamsCommand());
		m.registerCommand(new RegenCommand());
		m.registerCommand(new ReviveCommand());
		m.registerCommand(new ApplyDeathbanKit());
		m.registerCommand(new SetBalCommand());
		m.registerCommand(new PartnerPackageCommand());
		m.registerCommand(new SetEndExitCommand());
		m.registerCommand(new RenameCommand());
		m.registerCommand(new SetNetherBufferCommand());
		m.registerCommand(new SetWorldBorderCommand());
		m.registerCommand(new SetWorldBufferCommand());
		m.registerCommand(new GlowCommand());
		m.registerCommand(new CavernCommand());
		m.registerCommand(new CarePackageHandler());
		m.registerCommand(new SOTWCommand());
		m.registerCommand(new SpawnCommand());
		m.registerCommand(new SpawnDragonCommand());
		m.registerCommand(new TeamManageCommand());
		m.registerCommand(new TellLocationCommand());
		m.registerCommand(new ToggleChatCommand());
		m.registerCommand(new ToggleDeathMessagesCommand());
		m.registerCommand(new WipeDeathbansCommand());
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
		m.registerCommand(new KillstreaksCommand());
		m.registerCommand(new LeaderboardAddCommand());
		m.registerCommand(new StatModifyCommands());
		m.registerCommand(new StatsCommand());
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
		m.registerCommand(new QuestsCommand());
		m.registerCommand(new GuideCommand());
		m.registerCommand(new TeamChatSpyCommand());
		m.registerCommand(new AbilityCommand());
		m.registerCommand(new AbiltiesCommand());


	}



	@Override
	public void onDisable() {
		getEventHandler().saveEvents();

		for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
			getPlaytimeMap().playerQuit(player.getUniqueId(), false);
			player.setMetadata("loggedout", new FixedMetadataValue(this, true));
		}

		for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
			PvPClassHandler.getEquippedKits().get(playerName).remove(getServer().getPlayerExact(playerName));
		}

		for (Entity e : this.combatLoggerListener.getCombatLoggers()) {
			if (e != null) {
				e.remove();
			}
		}

		if (FrozenEconomyHandler.isInitiated()) {
			FrozenEconomyHandler.saveAll();
		}

		this.localJedisPool.close();
		this.backboneJedisPool.close();

		RedisSaveTask.save(null, false);
		Foxtrot.getInstance().getServerHandler().save();

		Foxtrot.getInstance().getMapHandler().getStatsHandler().save();

		RegenUtils.resetAll();
	}

	private void setupHandlers() {
		serverHandler = new ServerHandler();
		mapHandler = new MapHandler();
		mapHandler.load();
		setupHourEvents();

		abilityHandler = new AbilityHandler();
		abilityPackage = new AbilityPackage();

		new LunarClientHandler();

		chatGamesHandler = new ChatGamesHandler();


		teamHandler = new TeamHandler();
		LandBoard.getInstance().loadFromTeams();

		chatHandler = new ChatHandler();
		citadelHandler = new CitadelHandler();
		pvpClassHandler = new PvPClassHandler();
		eventHandler = new EventHandler();
		conquestHandler = new ConquestHandler();

		if (getConfig().getBoolean("glowstoneMountain", false)) {
			glowHandler = new GlowHandler();
		}

		if (getConfig().getBoolean("cavern", false)) {
			cavernHandler = new CavernHandler();
		}

		crateHandler = new CrateHandler();

		DeathMessageHandler.init();
		DTRHandler.loadDTR();
	}

	public <T> T runRedisCommand(RedisCommand<T> redisCommand) {
		Jedis jedis = this.localJedisPool.getResource();
		T result = null;
		try {
			result = (T)redisCommand.execute(jedis);
		} catch (Exception e) {
			e.printStackTrace();
			long localRedisLastError = System.currentTimeMillis();
			if (jedis != null) {
				this.localJedisPool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (jedis != null)
				this.localJedisPool.returnResource(jedis);
		}
		return result;
	}

	public <T> T runBackboneRedisCommand(RedisCommand<T> redisCommand) {
		Jedis jedis = this.backboneJedisPool.getResource();
		T result = null;
		try {
			result = (T)redisCommand.execute(jedis);
		} catch (Exception e) {
			e.printStackTrace();
			long backboneRedisLastError = System.currentTimeMillis();
			if (jedis != null) {
				this.backboneJedisPool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (jedis != null)
				this.backboneJedisPool.returnResource(jedis);
		}
		return result;
	}

	private void setupListeners() {
		getServer().getPluginManager().registerEvents(new MapListener(), this);
		getServer().getPluginManager().registerEvents(new AntiGlitchListener(), this);
		getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
		getServer().getPluginManager().registerEvents(new BorderListener(), this);
		getServer().getPluginManager().registerEvents((combatLoggerListener = new CombatLoggerListener()), this);
		getServer().getPluginManager().registerEvents(new CrowbarListener(), this);
		getServer().getPluginManager().registerEvents(new DeathbanListener(), this);
		getServer().getPluginManager().registerEvents(new EnchantmentLimiterListener(), this);
		getServer().getPluginManager().registerEvents(new EnderpearlCooldownHandler(), this);
		getServer().getPluginManager().registerEvents(new EndListener(), this);
		getServer().getPluginManager().registerEvents(new FoundDiamondsListener(), this);
		getServer().getPluginManager().registerEvents(new FoxListener(), this);
		getServer().getPluginManager().registerEvents(new GoldenAppleListener(), this);
		getServer().getPluginManager().registerEvents(new KOTHRewardKeyListener(), this);
		getServer().getPluginManager().registerEvents(new PvPTimerListener(), this);
		getServer().getPluginManager().registerEvents(new PotionLimiterListener(), this);
		getServer().getPluginManager().registerEvents(new NetherPortalListener(), this);
		getServer().getPluginManager().registerEvents(new PortalTrapListener(), this);
		getServer().getPluginManager().registerEvents(new SignSubclaimListener(), this);
		getServer().getPluginManager().registerEvents(new SpawnerTrackerListener(), this);
		getServer().getPluginManager().registerEvents(new SpawnListener(), this);
		getServer().getPluginManager().registerEvents(new SpawnTagListener(), this);
		getServer().getPluginManager().registerEvents(new StaffUtilsListener(), this);
		getServer().getPluginManager().registerEvents(new TeamListener(), this);
		getServer().getPluginManager().registerEvents(new WebsiteListener(), this);
		getServer().getPluginManager().registerEvents(new StatTrakListener(), this);
		getServer().getPluginManager().registerEvents(new ResourcePack(), this);
		getServer().getPluginManager().registerEvents(new AbilityPackageHandler(), this);

		if (getServerHandler().isReduceArmorDamage()) {
			getServer().getPluginManager().registerEvents(new ArmorDamageListener(), this);
		}

		if (getServerHandler().isBlockEntitiesThroughPortals()) {
			getServer().getPluginManager().registerEvents(new EntityPortalListener(), this);
		}

		if (getServerHandler().isBlockRemovalEnabled()) {
			getServer().getPluginManager().registerEvents(new BlockRegenListener(), this);
		}

		// Register kitmap specific listeners
		if (getServerHandler().isVeltKitMap() || getMapHandler().isKitMap()) {
			getServer().getPluginManager().registerEvents(new KitMapListener(), this);
			getServer().getPluginManager().registerEvents(new CarePackageHandler(), this);
		}

		getServer().getPluginManager().registerEvents(new BlockConvenienceListener(), this);

		//getServer().getPluginManager().registerEvents(new ChunkLimiterListener(), this );
		//getServer().getPluginManager().registerEvents(new IPListener(), this );
		//getServer().getPluginManager().registerEvents(new Prot3Sharp3Listener(), this);
	}

	private void setupPersistence() {
		(playtimeMap = new PlaytimeMap()).loadFromRedis();
		(oppleMap = new OppleMap()).loadFromRedis();
		(deathbanMap = new DeathbanMap()).loadFromRedis();
		(PvPTimerMap = new PvPTimerMap()).loadFromRedis();
		(startingPvPTimerMap = new StartingPvPTimerMap()).loadFromRedis();
		(deathsMap = new DeathsMap()).loadFromRedis();
		(killsMap = new KillsMap()).loadFromRedis();
		(chatModeMap = new ChatModeMap()).loadFromRedis();
		(toggleGlobalChatMap = new ToggleGlobalChatMap()).loadFromRedis();
		(fishingKitMap = new FishingKitMap()).loadFromRedis();
		(soulboundLivesMap = new SoulboundLivesMap()).loadFromRedis();
		(friendLivesMap = new FriendLivesMap()).loadFromRedis();
		(chatSpyMap = new ChatSpyMap()).loadFromRedis();
		(diamondMinedMap = new DiamondMinedMap()).loadFromRedis();
		(goldMinedMap = new GoldMinedMap()).loadFromRedis();
		(ironMinedMap = new IronMinedMap()).loadFromRedis();
		(coalMinedMap = new CoalMinedMap()).loadFromRedis();
		(redstoneMinedMap = new RedstoneMinedMap()).loadFromRedis();
		(lapisMinedMap = new LapisMinedMap()).loadFromRedis();
		(emeraldMinedMap = new EmeraldMinedMap()).loadFromRedis();
		(firstJoinMap = new FirstJoinMap()).loadFromRedis();
		(lastJoinMap = new LastJoinMap()).loadFromRedis();
		(enderPearlsUsedMap = new EnderPearlsUsedMap()).loadFromRedis();
		(expCollectedMap = new ExpCollectedMap()).loadFromRedis();
		(itemsRepairedMap = new ItemsRepairedMap()).loadFromRedis();
		(splashPotionsBrewedMap = new SplashPotionsBrewedMap()).loadFromRedis();
		(splashPotionsUsedMap = new SplashPotionsUsedMap()).loadFromRedis();
		(wrappedBalanceMap = new WrappedBalanceMap()).loadFromRedis();
		(toggleFoundDiamondsMap = new ToggleFoundDiamondsMap()).loadFromRedis();
		(toggleDeathMessageMap = new ToggleDeathMessageMap()).loadFromRedis();
		(tabListModeMap = new TabListModeMap()).loadFromRedis();
		(ipMap = new IPMap()).loadFromRedis();
		(whitelistedIPMap = new WhitelistedIPMap()).loadFromRedis();
		(cobblePickupMap = new CobblePickupMap()).loadFromRedis();
		(kdrMap = new KDRMap()).loadFromRedis();
		(raidableTeamsMap = new RaidableTeamsMap()).loadFromRedis();


		if (getServerHandler().isVeltKitMap() || getMapHandler().isKitMap()) {
			(tokensMap = new KitmapTokensMap()).loadFromRedis();
		}
	}


	private void setupHourEvents() {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("Foxtrot - Hour Event Thread").setDaemon(true).build());
		int minOfHour = Calendar.getInstance().get(Calendar.MINUTE);
		int minToHour = 60 - minOfHour;
		executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new HourEvent(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)))), minToHour, 60L, TimeUnit.MINUTES);
	}

}

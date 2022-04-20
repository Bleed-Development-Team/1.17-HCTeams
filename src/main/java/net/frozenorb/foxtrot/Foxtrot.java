package net.frozenorb.foxtrot;

import co.aikar.commands.BukkitCommandManager;
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
import me.vaperion.blade.Blade;
import me.vaperion.blade.bindings.impl.BukkitBindings;
import me.vaperion.blade.container.impl.BukkitCommandContainer;
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
import net.frozenorb.foxtrot.events.dtc.commands.DTCCreateCommand;
import net.frozenorb.foxtrot.events.koth.commands.koth.KOTHCommand;
import net.frozenorb.foxtrot.events.region.carepackage.CarePackageHandler;
import net.frozenorb.foxtrot.events.region.cavern.CavernHandler;
import net.frozenorb.foxtrot.events.region.cavern.commands.CavernCommand;
import net.frozenorb.foxtrot.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.events.region.glowmtn.commands.GlowCommand;
import net.frozenorb.foxtrot.extras.abusing.cheats.commands.ReachCommand;
import net.frozenorb.foxtrot.extras.abusing.cheats.impl.ReachCheat;
import net.frozenorb.foxtrot.extras.blockshop.command.BlockShopCommand;
import net.frozenorb.foxtrot.extras.discord.Discord;
import net.frozenorb.foxtrot.extras.gems.GemsHandler;
import net.frozenorb.foxtrot.extras.gems.commands.GemShopCommand;
import net.frozenorb.foxtrot.extras.gems.commands.admincommands.AddToCheckOutCommand;
import net.frozenorb.foxtrot.extras.gems.commands.admincommands.ClearCartCommand;
import net.frozenorb.foxtrot.extras.gems.commands.admincommands.GemAddCommand;
import net.frozenorb.foxtrot.extras.gems.map.GemMap;
import net.frozenorb.foxtrot.extras.lunar.LunarClientHandler;
import net.frozenorb.foxtrot.extras.lunar.nametag.ClientNametagProvider;
import net.frozenorb.foxtrot.extras.redeem.RedeemCreatorCommand;
import net.frozenorb.foxtrot.extras.redeem.RedeemCreatorHandler;
import net.frozenorb.foxtrot.extras.resoucepack.ResourcePack;
import net.frozenorb.foxtrot.extras.sell.command.SellShopCommand;
import net.frozenorb.foxtrot.extras.settings.commands.SettingsCommand;
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
import net.frozenorb.foxtrot.server.commands.betrayer.*;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.team.TeamType;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.commands.*;
import net.frozenorb.foxtrot.team.commands.pvp.PvPCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamChatCommand;
import net.frozenorb.foxtrot.team.commands.team.subclaim.*;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRBitmaskType;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.HourEvent;
import net.frozenorb.foxtrot.util.RegenUtils;
import net.frozenorb.foxtrot.util.help.HelpGenerator;
import net.frozenorb.foxtrot.util.provider.FloatProvider;
import net.frozenorb.foxtrot.util.provider.IntegerProvider;
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

import javax.security.auth.login.LoginException;
import java.util.Calendar;
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
	@Getter private GlowHandler glowHandler;
	@Getter private CrateHandler crateHandler;

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
			this.localJedisPool = new JedisPool(new JedisPoolConfig(), "redis-19823.c258.us-east-1-4.ec2.cloud.redislabs.com", 19823, 20000, "yQ4evkvAStkc3eXTjDX4rFeMKFTr2zZg");
			System.out.println("Connected to the local Jedis pool.");

		} catch (Exception e) {
			this.localJedisPool = null;
			e.printStackTrace();
		}

		try {
			this.backboneJedisPool = new JedisPool(new JedisPoolConfig(), "redis-19823.c258.us-east-1-4.ec2.cloud.redislabs.com", 19823, 20000, "yQ4evkvAStkc3eXTjDX4rFeMKFTr2zZg");
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


		for (World world : Bukkit.getWorlds()) {
			world.setThundering(false);
			world.setStorm(false);
			world.setWeatherDuration(Integer.MAX_VALUE);
			world.setGameRule(GameRule.DO_FIRE_TICK, false);
			world.setGameRule(GameRule.MOB_GRIEFING, false);
		}
		Cooldown.createCooldown("lff");
		BukkitCommandManager m = new BukkitCommandManager(this);

		m.getCommandContexts().registerContext(Team.class, c -> getTeamHandler().getTeams().stream().filter(team -> team.getName().equalsIgnoreCase(c.popFirstArg())).findFirst().orElse(null));


		Blade.of()
				.fallbackPrefix("hcteams")
				.containerCreator(BukkitCommandContainer.CREATOR)
				.bind(Team.class, new TeamType()).bind(Integer.class, new IntegerProvider()).bind(Float.class, new FloatProvider()).bind(DTRBitmask.class, new DTRBitmaskType()).bind(Event.class, new EventParameterType()).bind(StatsTopCommand.StatsObjective.class, new StatsTopCommand.StatsObjectiveProvider())






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
		(gemsMap = new GemMap()).loadFromRedis();
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

package net.frozenorb.foxtrot;

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
import net.frozenorb.foxtrot.chat.trivia.TriviaHandler;
import net.frozenorb.foxtrot.gameplay.ability.partnerpackages.PartnerPackageHandler;
import net.frozenorb.foxtrot.chat.ChatHandler;
import net.frozenorb.foxtrot.commands.CommandHandler;
import net.frozenorb.foxtrot.commands.op.*;
import net.frozenorb.foxtrot.commands.op.eotw.EOTWHandler;
import net.frozenorb.foxtrot.crates.CrateHandler;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import net.frozenorb.foxtrot.gameplay.airdrops.AirDropHandler;
import net.frozenorb.foxtrot.gameplay.archerupgrades.ArcherUpgradeHandler;
import net.frozenorb.foxtrot.gameplay.clickable.ClickableItemHandler;
import net.frozenorb.foxtrot.gameplay.events.EventHandler;
import net.frozenorb.foxtrot.gameplay.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.gameplay.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.gameplay.events.mad.MadHandler;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.CavernHandler;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.gameplay.enchants.CustomEnchant;
import net.frozenorb.foxtrot.gameplay.enchants.events.EventAnalyser;
import net.frozenorb.foxtrot.gameplay.lunar.LunarClientHandler;
import net.frozenorb.foxtrot.gameplay.lunar.nametag.ClientNametagProvider;
import net.frozenorb.foxtrot.map.MapHandler;
import net.frozenorb.foxtrot.provider.nametags.NametagManager;
import net.frozenorb.foxtrot.walls.WallsHandler;
import net.frozenorb.foxtrot.persist.RedisSaveTask;
import net.frozenorb.foxtrot.persist.maps.*;
import net.frozenorb.foxtrot.persist.maps.statistics.*;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.team.upgrades.UpgradeHandler;
import net.frozenorb.foxtrot.util.redis.RedisCommand;
import net.frozenorb.foxtrot.provider.scoreboard.HCFScoreboardProvider;
import net.frozenorb.foxtrot.server.listener.ListenerHandler;
import net.frozenorb.foxtrot.server.listener.impl.*;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.HourEvent;
import net.frozenorb.foxtrot.util.RegenUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HCF extends JavaPlugin {

	public static String MONGO_DB_NAME = "HCTeams";

	@Getter private static HCF instance;

	@Getter private MongoClient mongoPool;

	@Getter private ChatHandler chatHandler;
	@Getter private PvPClassHandler pvpClassHandler;
	@Getter private TeamHandler teamHandler;
	@Getter private ServerHandler serverHandler;
	@Getter private MapHandler mapHandler;
	@Getter private CitadelHandler citadelHandler;
	@Getter private EventHandler eventHandler;
	@Getter private ConquestHandler conquestHandler;
	@Getter private MadHandler madHandler;
	@Getter private CavernHandler cavernHandler;
	@Getter private GlowHandler glowHandler;
	@Getter private CrateHandler crateHandler;
	@Getter private EOTWHandler eotwHandler;
	@Getter private LunarClientHandler lunarClientHandler;
	@Getter private NametagManager nametagManager;
	@Getter private PartnerPackageHandler partnerPackageHandler;
	@Getter private ClickableItemHandler clickableItemHandler;
	@Getter private UpgradeHandler upgradeHandler;
	@Getter private TriviaHandler triviaHandler;
	@Getter private ArcherUpgradeHandler archerUpgradeHandler;
	@Getter private AirDropHandler airDropHandler;
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
	@Getter private IPMap ipMap;
	@Getter private WhitelistedIPMap whitelistedIPMap;
	@Getter private CobblePickupMap cobblePickupMap;
	@Getter private KDRMap kdrMap;
	@Getter private KitmapTokensMap tokensMap;
	@Getter private RaidableTeamsMap raidableTeamsMap;
	@Getter private ReclaimMap reclaimMap;
	@Getter private RedeemMap redeemMap;
	@Getter private GemsMap gemsMap;

	public static String world = "world";

	private final boolean whitelisted = true;

	@Getter public CombatLoggerListener combatLoggerListener;
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
		try {
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
				mongoPool = new MongoClient(new MongoClientURI("mongodb+srv://embry:xv1ul2AcFKtLO07g@frozen.spewtzz.mongodb.net/"));
				MONGO_DB_NAME = Bukkit.getServer().getName();

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				this.localJedisPool = new JedisPool(new JedisPoolConfig(), "redis-14992.c10.us-east-1-2.ec2.cloud.redislabs.com", 14992, 20000, "6xMlr27F3lIBhSp5UIDsX1Oe9B5xn5k8");
				System.out.println("Connected to the local Jedis pool.");

			} catch (Exception e) {
				this.localJedisPool = null;
				e.printStackTrace();
			}

			try {
				this.backboneJedisPool = new JedisPool(new JedisPoolConfig(), "redis-14992.c10.us-east-1-2.ec2.cloud.redislabs.com", 14992, 20000, "6xMlr27F3lIBhSp5UIDsX1Oe9B5xn5k8");
				System.out.println("Connected to the Backbone Jedis pool.");
			} catch (Exception e) {
				this.backboneJedisPool = null;
				e.printStackTrace();
			}

			(new DTRHandler()).runTaskTimer(this, 20L, 1200L);
			(new RedisSaveTask()).runTaskTimerAsynchronously(this, 1200L, 1200L);
			(new WallsHandler()).start();


			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ClientNametagProvider(), 0L, 40L);
			//Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Webhook.sendKothSchedule(), 0, 72000L);

			setupHandlers();
			setupPersistence();
			setupTasks();

			new CommandHandler(this);
			new ListenerHandler(this);
			new MenuHandler(this);

			Assemble assemble = new Assemble(this, new HCFScoreboardProvider());

			assemble.setTicks(2);
			assemble.setAssembleStyle(AssembleStyle.KOHI);

			for (World world : Bukkit.getWorlds()) {
				world.setThundering(false);
				world.setStorm(false);
				world.setWeatherDuration(Integer.MAX_VALUE);
				world.setGameRule(GameRule.DO_FIRE_TICK, false);
				world.setGameRule(GameRule.MOB_GRIEFING, false);
			}

			if (whitelisted) {
				getServer().setWhitelist(true);
			}

			//new ActionbarThread().start();

			Bukkit.getPluginManager().registerEvents(new EventAnalyser(this), this);
			CustomEnchant.init();
		} catch (Exception ex){
			ex.printStackTrace();
			Bukkit.getServer().shutdown(); // so if the plugin doesnt load and someone joins, it doesnt let you even grief ppls bases, etc.
		}
	}

	@Override
	public void onDisable() {
		getEventHandler().saveEvents();

		for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
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

		if (EconomyHandler.isInitiated()) {
			EconomyHandler.saveAll();
		}

		this.localJedisPool.close();
		this.backboneJedisPool.close();

		RedisSaveTask.save(null, false);
		HCF.getInstance().getServerHandler().save();

		HCF.getInstance().getMapHandler().getStatsHandler().save();

		RegenUtils.resetAll();
	}

	private void setupHandlers() {
		serverHandler = new ServerHandler();
		mapHandler = new MapHandler();
		mapHandler.load();
		setupHourEvents();

		lunarClientHandler = new LunarClientHandler();

		madHandler = new MadHandler();

		nametagManager = new NametagManager();

		teamHandler = new TeamHandler();
		LandBoard.getInstance().loadFromTeams();

		upgradeHandler = new UpgradeHandler();
		clickableItemHandler = new ClickableItemHandler();
		triviaHandler = new TriviaHandler();
		archerUpgradeHandler = new ArcherUpgradeHandler();
		airDropHandler = new AirDropHandler(this);
		abilityHandler = new AbilityHandler();

		chatHandler = new ChatHandler();
		citadelHandler = new CitadelHandler();
		pvpClassHandler = new PvPClassHandler();
		eventHandler = new EventHandler();
		conquestHandler = new ConquestHandler();
		eotwHandler = new EOTWHandler();
		if (getConfig().getBoolean("glowstoneMountain", false)) {
			glowHandler = new GlowHandler();
		}
		partnerPackageHandler = new PartnerPackageHandler();

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
		(ipMap = new IPMap()).loadFromRedis();
		(whitelistedIPMap = new WhitelistedIPMap()).loadFromRedis();
		(cobblePickupMap = new CobblePickupMap()).loadFromRedis();
		(kdrMap = new KDRMap()).loadFromRedis();
		(raidableTeamsMap = new RaidableTeamsMap()).loadFromRedis();
		(reclaimMap = new ReclaimMap()).loadFromRedis();
		(redeemMap = new RedeemMap()).loadFromRedis();
		(gemsMap = new GemsMap()).loadFromRedis();


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

	private boolean hasClaimedLocked = false;
	private boolean a = false;
	private boolean b = false;
	private boolean c = false;

	private void setupTasks() {
		// unlocks claims at 10 minutes left of SOTW timer
		new BukkitRunnable() {
			@Override
			public void run() {
				if (CustomTimerCreateCommand.isSOTWTimer()) {
					long endsAt = CustomTimerCreateCommand.getCustomTimers().get("&a&lSOTW");
					if (endsAt - System.currentTimeMillis() <= TimeUnit.MINUTES.toMillis(10L) && !hasClaimedLocked) {
						for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
							if (team.isClaimLocked()) {
								team.setClaimLocked(false);
							}
						}

						Bukkit.broadcastMessage(CC.translate("&fAll claims have been &cunlocked &fdue to SOTW ending in 10 minutes!"));
						Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(CC.translate("&6&lSOTW"), CC.translate("&6&l10 minutes remaining!")));

						hasClaimedLocked = true;
					} else if (endsAt - System.currentTimeMillis() <= TimeUnit.MINUTES.toMillis(5L) && !a) {
						Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(CC.translate("&6&lSOTW"), CC.translate("&6&l5 minutes remaining!")));
						a = true;
					} else if (endsAt - System.currentTimeMillis() <= TimeUnit.SECONDS.toMillis(1L) && !b){
						Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(CC.translate("&6&lSOTW"), CC.translate("&6&lhas ended!")));
						b = true;
					} else if (endsAt - System.currentTimeMillis() <= TimeUnit.MINUTES.toMillis(30L) && !c){
						Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(CC.translate("&6&lSOTW"), CC.translate("&6&l30 minutes remaining!")));
						c = true;
					}
				} else {
					hasClaimedLocked = false;
					a = false;
					b = false;
					c = false;
				}
			}
		}.runTaskTimerAsynchronously(HCF.getInstance(), 20L, 20L);

		Bukkit.getScheduler().runTaskTimer(this, () -> {
			for (Entity entity : Bukkit.getWorld("world").getEntities().stream().filter(it -> !(it instanceof Player) && !(it instanceof Item)).toList()){
				entity.remove();
			}
		}, 0, 20L * 10);
	}

}
package net.frozenorb.foxtrot.gameplay.events;

import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.koth.listeners.KOTHListener;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EventHandler {

	@Getter private Set<Event> events = new HashSet<>();
	@Getter private Map<EventScheduledTime, String> EventSchedule = new TreeMap<>();

	@Getter
	@Setter
	private boolean scheduleEnabled;

	public EventHandler() {
		loadEvents();
		loadSchedules();

		HCF.getInstance().getServer().getPluginManager().registerEvents(new KOTHListener(), HCF.getInstance());
		HCF.getInstance().getServer().getPluginManager().registerEvents(new EventListener(), HCF.getInstance());

		new BukkitRunnable() {
			public void run() {
				for (Event event : events) {
					if (event.isActive()) {
						event.tick();
					}
				}
			}
		}.runTaskTimer(HCF.getInstance(), 5L, 20L);

		HCF.getInstance().getServer().getScheduler().runTaskTimer(HCF.getInstance(), () -> {
			terminateKOTHs();
			activateKOTHs();
		}, 20L, 20L);
		// The initial delay of 5 ticks is to 'offset' us with the scoreboard handler.
	}

	public void loadEvents() {
		try {
			File eventsBase = new File(HCF.getInstance().getDataFolder(), "events");

			if (!eventsBase.exists()) {
				eventsBase.mkdir();
			}

			for (EventType eventType : EventType.values()) {
				File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());

				if (!subEventsBase.exists()) {
					subEventsBase.mkdir();
				}

				for (File eventFile : subEventsBase.listFiles()) {
					if (eventFile.getName().endsWith(".json")) {
						events.add(HCF.GSON.fromJson(FileUtils.readFileToString(eventFile), KOTH.class));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// look for a previously active Event, if present deactivate and start it after 15 seconds
		events.stream().filter(Event::isActive).findFirst().ifPresent((event) -> {
			event.setActive(false);
			Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
				// if anyone had started a Event within the last 15 seconds,
				// don't activate previously active one
				if (events.stream().noneMatch(Event::isActive)) {
					event.activate();
				}
			}, 300L);
		});
	}

	public void fillSchedule() {
		List<String> allevents = new ArrayList<>();

		for (Event event : getEvents()) {
			if (event.isHidden() || event.getName().equalsIgnoreCase("EOTW") || event.getName().equalsIgnoreCase("Citadel")) {
				continue;
			}

			allevents.add(event.getName());
		}

		for (int minute = 0; minute < 60; minute++) {
			for (int hour = 0; hour < 24; hour++) {
				this.EventSchedule.put(new EventScheduledTime(Calendar.getInstance().get(Calendar.DAY_OF_YEAR), (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + hour) % 24, minute), allevents.get(0));
			}
		}
	}

	public void loadSchedules() {
		EventSchedule.clear();

		try {
			File eventSchedule = new File(HCF.getInstance().getDataFolder(), "eventSchedule.json");

			if (!eventSchedule.exists()) {
				eventSchedule.createNewFile();
				BasicDBObject schedule = new BasicDBObject();
				int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
				List<String> allevents = new ArrayList<>();

				for (Event event : getEvents()) {
					if (event.isHidden() || event.getName().equalsIgnoreCase("EOTW") || event.getName().equalsIgnoreCase("Citadel")) {
						continue;
					}

					allevents.add(event.getName());
				}

				for (int dayOffset = 0; dayOffset < 21; dayOffset++) {
					int day = (currentDay + dayOffset) % 365;
					EventScheduledTime[] times = new EventScheduledTime[]{

							new EventScheduledTime(day, 0, 30), // 00:30am EST
							new EventScheduledTime(day, 8, 30), // 03:30am EST
							new EventScheduledTime(day, 16, 30), // 06:30am EST
							new EventScheduledTime(day, 24, 30), // 09:30am EST

					};

					Collections.shuffle(allevents);

					if (!allevents.isEmpty()) {
						for (int eventTimeIndex = 0; eventTimeIndex < times.length; eventTimeIndex++) {
							EventScheduledTime eventTime = times[eventTimeIndex];
							String eventName = allevents.get(eventTimeIndex % allevents.size());

							schedule.put(eventTime.toString(), eventName);
						}
					}
				}

				FileUtils.write(eventSchedule, HCF.GSON.toJson(new JsonParser().parse(schedule.toString())));
			}

			BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(eventSchedule));

			if (dbo != null) {
				for (Map.Entry<String, Object> entry : dbo.entrySet()) {
					EventScheduledTime scheduledTime = EventScheduledTime.parse(entry.getKey());
					this.EventSchedule.put(scheduledTime, String.valueOf(entry.getValue()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveEvents() {
		try {
			File eventsBase = new File(HCF.getInstance().getDataFolder(), "events");

			if (!eventsBase.exists()) {
				eventsBase.mkdir();
			}

			for (EventType eventType : EventType.values()) {

				File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());

				if (!subEventsBase.exists()) {
					subEventsBase.mkdir();
				}

				for (File eventFile : Objects.requireNonNull(subEventsBase.listFiles())) {
					eventFile.delete();
				}
			}

			for (Event event : events) {
				File eventFile = new File(new File(eventsBase, event.getType().name().toLowerCase()), event.getName() + ".json");
				FileUtils.write(eventFile, HCF.GSON.toJson(event));
				Bukkit.getLogger().info("Writing " + event.getName() + " to " + eventFile.getAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Event getEvent(String name) {
		for (Event event : events) {
			if (event.getName().equalsIgnoreCase(name)) {
				return (event);
			}
		}

		return (null);
	}

	private void activateKOTHs() {
		// Don't start a KOTH during EOTW.
		if (HCF.getInstance().getServerHandler().isPreEOTW()) {
			return;
		}

		// Don't start a KOTH if another one is active.
		for (Event koth : HCF.getInstance().getEventHandler().getEvents()) {
			if (koth.isActive()) {
				return;
			}
		}

		EventScheduledTime scheduledTime = EventScheduledTime.parse(new Date());

		if (HCF.getInstance().getEventHandler().getEventSchedule().containsKey(scheduledTime)) {
			String resolvedName = HCF.getInstance().getEventHandler().getEventSchedule().get(scheduledTime);
			Event resolved = HCF.getInstance().getEventHandler().getEvent(resolvedName);

			if (scheduledTime.getHour() == 15 && scheduledTime.getMinutes() == 30 && resolvedName.equals("Conquest")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "conquestadmin start");
				return;
			}

			if (resolved == null) {
				HCF.getInstance().getLogger().warning("The event scheduler has a schedule for an event named " + resolvedName + ", but the event does not exist.");
				return;
			}

			if (HCF.getInstance().getConfig().getBoolean("legions") && Bukkit.getOnlinePlayers().size() < 30) {
				EventSchedule.remove(scheduledTime);
				HCF.getInstance().getLogger().warning("The event scheduler cannot start an event w/ under 30 players on Legions.");
				return;
			}

			resolved.activate();
		}
	}

	private void terminateKOTHs() {
		EventScheduledTime nextScheduledTime = EventScheduledTime.parse(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)));

		if (HCF.getInstance().getEventHandler().getEventSchedule().containsKey(nextScheduledTime)) {
			// We have a KOTH about to start. Prepare for it.
			for (Event activeEvent : HCF.getInstance().getEventHandler().getEvents()) {
				if (activeEvent.getType() != EventType.KOTH) {
					continue;
				}
				KOTH activeKoth = (KOTH) activeEvent;
				if (!activeKoth.isHidden() && activeKoth.isActive() && !activeKoth.getName().equals("Citadel") && !activeKoth.getName().equals("EOTW") && !activeKoth.getName().equals("Mad")) {
					if (activeKoth.getCurrentCapper() != null && !activeKoth.isTerminate()) {
						activeKoth.setTerminate(true);
						HCF.getInstance().getServer().broadcastMessage(CC.translate("&3&l[KOTH] " + ChatColor.BLUE + activeKoth.getName() + ChatColor.WHITE + " will be terminated if knocked."));
					} else {
						activeKoth.deactivate();
						HCF.getInstance().getServer().broadcastMessage(CC.translate("&3&l[KOTH] " + ChatColor.BLUE + activeKoth.getName() + ChatColor.WHITE + " will be terminated."));
					}
				}
			}
		}
	}

}

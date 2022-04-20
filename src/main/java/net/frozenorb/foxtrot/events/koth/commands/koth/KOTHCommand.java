package net.frozenorb.foxtrot.events.koth.commands.koth;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.EventScheduledTime;
import net.frozenorb.foxtrot.events.EventType;
import net.frozenorb.foxtrot.events.koth.KOTH;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.bukkit.ChatColor.*;
@CommandAlias("koth|event|events")
public class KOTHCommand extends BaseCommand {
    public static final DateFormat KOTH_DATE_FORMAT = new SimpleDateFormat("EEE h:mm a");


    @Subcommand("activate")
    @Description("Activate an event")
    @CommandPermission("foxtrot.event.koth.activate")
    public static void kothActivate(Player sender, Event koth) {
        // Don't start a KOTH if another one is active.
        for (Event otherKoth : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (otherKoth.isActive()) {
                sender.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active.");
                return;
            }
        }

        if( (koth.getName().equalsIgnoreCase("citadel") || koth.getName().toLowerCase().contains("conquest")) && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Only ops can use the activate command for weekend events.");
            return;
        }

        koth.activate();
        sender.sendMessage(ChatColor.GRAY + "Activated " + koth.getName() + ".");
    }


    @Subcommand("create")
    @Description("Create a new event")
    @CommandPermission("foxtrot.event.koth.create")
    public static void kothCreate(Player sender, String koth) {
        new KOTH(koth, sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created a KOTH named " + koth + ".");
    }
    @Subcommand("deactivate|inactive")
    @CommandPermission("foxtrot.event.koth.deactivate")
    @Description("Deactivate an event")
    public static void kothDectivate(CommandSender sender, Event koth) {
        koth.deactivate();
        sender.sendMessage(ChatColor.GRAY + "Deactivated " + koth.getName() + " event.");
    }

    @Subcommand("delete")
    @Description("Delete an event")
    @CommandPermission("foxtrot.event.koth.delete")
    public static void kothDelete(Player sender, Event koth) {
        Foxtrot.getInstance().getEventHandler().getEvents().remove(koth);
        Foxtrot.getInstance().getEventHandler().saveEvents();
        sender.sendMessage(ChatColor.GRAY + "Deleted event " + koth.getName() + ".");
    }

    @Subcommand("dist|distance")
    @Description("Set the distance of a KOTH Capture Zone!")
    @CommandPermission("foxtrot.event.koth.distance")
    public static void kothDist(Player sender, Event koth, Integer distance) {
        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Can only set distance for KOTHs");
            return;
        }

        ((KOTH) koth).setCapDistance(distance);
        sender.sendMessage(ChatColor.GRAY + "Set max distance for the " + koth.getName() + " KOTH.");
    }

    @Subcommand("help")
    @Description("View help for an event")
    @CommandPermission("foxtrot.event.koth.help")
    @HelpCommand
    public static void kothHelp(Player sender) {
        sender.sendMessage(ChatColor.RED + "/koth list - Lists KOTHs");
        sender.sendMessage(ChatColor.RED + "/koth activate <name> - Activates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth deactivate <name> - Deactivates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth loc <name> - Set a KOTH's cap location");
        sender.sendMessage(ChatColor.RED + "/koth time <name> <time> - Sets a KOTH's cap time");
        sender.sendMessage(ChatColor.RED + "/koth dist <name> <distance> - Sets a KOTH's cap distance");
        sender.sendMessage(ChatColor.RED + "/koth tp <name> - TPs to a KOTH's");
        sender.sendMessage(ChatColor.RED + "/koth create <name> - Creates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth delete <name> - Deletes a KOTH");
    }

    @Subcommand("hidden")
    @Description("Toggles whether a KOTH is hidden")
    @CommandPermission("foxtrot.event.koth.hidden")
    public static void kothHidden(Player sender, Event koth, boolean hidden) {
        koth.setHidden(hidden);
        sender.sendMessage(ChatColor.GRAY + "Set visibility for the " + koth.getName() + " event.");
    }

    @Subcommand("list")
    @Description("List all events")
    @CommandPermission("foxtrot.event.koth.list")
    public static void kothList(Player sender) {
        if (Foxtrot.getInstance().getEventHandler().getEvents().isEmpty()) {
            sender.sendMessage(RED + "There aren't any events set.");
            return;
        }

        for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (event.getType() == EventType.KOTH) {
                KOTH koth = (KOTH) event;
                sender.sendMessage((koth.isHidden() ? DARK_GRAY + "[H] " : "") + (koth.isActive() ? GREEN : RED) + koth.getName() + WHITE + " - " + GRAY + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()) + DARK_GRAY + "/" + GRAY + TimeUtils.formatIntoMMSS(koth.getCapTime()) + " " + WHITE + "- " + GRAY + (koth.getCurrentCapper() == null ? "None" : koth.getCurrentCapper()));
            }
        }
    }

    @Subcommand("loc")
    @Description("Set a KOTH's cap location")
    @CommandPermission("foxtrot.event.koth.loc")
    public static void kothLoc(Player sender, Event koth) {
        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Unable to set location for a non-KOTH event.");
        } else {
            ((KOTH) koth).setLocation(sender.getLocation());
            sender.sendMessage(ChatColor.GRAY + "Set cap location for the " + koth.getName() + " KOTH.");
        }
    }
    @Subcommand("schedule")
    public static void kothSchedule(Player sender) {
        int sent = 0;
        Date now = new Date();

        for (Map.Entry<EventScheduledTime, String> entry : Foxtrot.getInstance().getEventHandler().getEventSchedule().entrySet()) {
            Event resolved = Foxtrot.getInstance().getEventHandler().getEvent(entry.getValue());

            if (resolved == null || resolved.isHidden() || !entry.getKey().toDate().after(now) || resolved.getType() != EventType.KOTH) {
                continue;
            }

            if (sent > 5) {
                break;
            }

            sent++;
            sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + entry.getValue() + ChatColor.GOLD + " can be captured at " + ChatColor.BLUE + KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + ChatColor.GOLD + ".");
        }

        if (sent == 0) {
            sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.RED + "KOTH Schedule: " + ChatColor.YELLOW + "Undefined");
        } else {
            sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + KOTH_DATE_FORMAT.format(new Date()) + ChatColor.GOLD + ".");
        }
    }

    @Subcommand("time")
    @Description("Set a KOTH's cap time")
    @CommandPermission("foxtrot.event.koth.time")
    public static void kothTime(Player sender, Event koth, Float time) {
        if (time > 20F) {
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "This command was changed! The time parameter is now in minutes, not seconds. For example, to set a KOTH's capture time to 20 minutes 30 seconds, use /koth time 20.5");
        }

        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Unable to modify cap time for a non-KOTH event.");
        } else {
            ((KOTH) koth).setCapTime((int) (time * 60F));
            sender.sendMessage(ChatColor.GRAY + "Set cap time for the " + koth.getName() + " KOTH.");
        }
    }


    @Subcommand("tp")
    @Description("Teleport to a KOTH's cap location")
    @CommandPermission("foxtrot.event.koth.tp")
    public static void kothTP(Player sender, Event koth) {
        if (koth.getType() == EventType.KOTH) {
            sender.teleport(((KOTH) koth).getCapLocation().toLocation(Foxtrot.getInstance().getServer().getWorld(((KOTH) koth).getWorld())));
            sender.sendMessage(ChatColor.GRAY + "Teleported to the " + koth.getName() + " KOTH.");
        } else if (koth.getType() == EventType.DTC) {
            sender.teleport(((KOTH) koth).getCapLocation().toLocation(Foxtrot.getInstance().getServer().getWorld(((KOTH) koth).getWorld())));
            sender.sendMessage(ChatColor.GRAY + "Teleported to the " + koth.getName() + " DTC.");
        }

        sender.sendMessage(ChatColor.RED + "You can't TP to an event that doesn't have a location.");
    }

    @Default
    @Description("View information about an event")
    public static void koth(Player sender) {
        for (Event koth : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (!koth.isHidden() && koth.isActive()) {
                FancyMessage fm = new FancyMessage("[Events] ")
                        .color(GOLD)
                        .then(koth.getName())
                        .color(YELLOW) // koth name should be yellow
                        .style(UNDERLINE);
                if (koth instanceof KOTH) {
                    fm.tooltip(YELLOW.toString() + ((KOTH) koth).getCapLocation().getBlockX() + ", " + ((KOTH) koth).getCapLocation().getBlockZ());
                }
                fm.color(YELLOW) // should color Event coords gray
                        .then(" can be contested now.")
                        .color(GOLD);
                fm.send(sender);
                return;
            }
        }

        Date now = new Date();

        for (Map.Entry<EventScheduledTime, String> entry : Foxtrot.getInstance().getEventHandler().getEventSchedule().entrySet()) {
            if (entry.getKey().toDate().after(now)) {
                sender.sendMessage(GOLD + "[KingOfTheHill] " + YELLOW + entry.getValue() + GOLD + " can be captured at " + BLUE + KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + GOLD + ".");
                sender.sendMessage(GOLD + "[KingOfTheHill] " + YELLOW + "It is currently " + BLUE + KOTH_DATE_FORMAT.format(now) + GOLD + ".");
                sender.sendMessage(YELLOW + "Type '/koth schedule' to see more upcoming Events.");
                return;
            }
        }

        sender.sendMessage(GOLD + "[KingOfTheHill] " + RED + "Next Event: " + YELLOW + "Undefined");
    }

}
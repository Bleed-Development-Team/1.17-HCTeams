package net.frozenorb.foxtrot.gameplay.events;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;;
import net.frozenorb.foxtrot.HCF;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EventParameterType implements ContextResolver<Event, BukkitCommandExecutionContext> {

    @Override
    public Event getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();
        Player sender = arg.getPlayer();

        if (sender == null) throw new InvalidCommandArgument("There was an error whilst attempting to process this command.");

        if (source.equals("active")) {
            for (Event event : HCF.getInstance().getEventHandler().getEvents()) {
                if (event.isActive() && !event.isHidden()) {
                    return event;
                }
            }

            throw new InvalidCommandArgument(ChatColor.RED + "There is no active event at the moment.");

        }

        Event event = HCF.getInstance().getEventHandler().getEvent(source);

        if (event == null) {
            throw new InvalidCommandArgument(ChatColor.RED + "No event with the name " + source + " found.");
        }

        return (event);
    }
}
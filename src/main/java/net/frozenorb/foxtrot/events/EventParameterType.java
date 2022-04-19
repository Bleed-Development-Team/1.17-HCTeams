package net.frozenorb.foxtrot.events;

import me.vaperion.blade.argument.BladeArgument;
import me.vaperion.blade.argument.BladeProvider;
import me.vaperion.blade.context.BladeContext;
import me.vaperion.blade.exception.BladeExitMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EventParameterType implements BladeProvider<Event> {

    @Override
    public @Nullable Event provide(@NotNull BladeContext context, @NotNull BladeArgument argument) throws BladeExitMessage {
        String source = argument.getString();
        Player sender = context.sender().parseAs(Player.class);

        if (sender == null) throw new BladeExitMessage("There was an error whilst attempting to process this command.");

        if (source.equals("active")) {
            for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
                if (event.isActive() && !event.isHidden()) {
                    return event;
                }
            }

            throw new BladeExitMessage(ChatColor.RED + "There is no active event at the moment.");

        }

        Event event = Foxtrot.getInstance().getEventHandler().getEvent(source);

        if (event == null) {
            throw new BladeExitMessage(ChatColor.RED + "No event with the name " + source + " found.");
        }

        return (event);
        }

    @Override
    public @NotNull List<String> suggest(@NotNull BladeContext context, @NotNull BladeArgument argument) throws BladeExitMessage {
        List<String> completions = new ArrayList<>();
        String source = argument.getString();

        for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (StringUtils.startsWithIgnoreCase(event.getName(), source)) {
                completions.add(event.getName());
            }
        }

        return (completions);
    }
}
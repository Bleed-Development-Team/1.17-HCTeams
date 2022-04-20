package net.frozenorb.foxtrot.team.dtr;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import org.bukkit.entity.Player;

public class DTRBitmaskType implements ContextResolver<DTRBitmask, BukkitCommandExecutionContext> {

    @Override
    public DTRBitmask getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        Player player = arg.getPlayer();
        String source = arg.getFirstArg();
        for (DTRBitmask bitmaskType : DTRBitmask.values()) {
            if (source.equalsIgnoreCase(bitmaskType.getName())) {
                return (bitmaskType);
            }
        }

        throw new InvalidCommandArgument("No bitmask type with the name " + source + " found.");
    }
}
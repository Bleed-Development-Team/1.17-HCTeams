package net.frozenorb.foxtrot.team.dtr;

import me.vaperion.blade.argument.BladeArgument;
import me.vaperion.blade.argument.BladeProvider;
import me.vaperion.blade.context.BladeContext;
import me.vaperion.blade.exception.BladeExitMessage;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DTRBitmaskType implements BladeProvider<DTRBitmask> {


    @Override
    public @Nullable DTRBitmask provide(@NotNull BladeContext context, @NotNull BladeArgument argument) throws BladeExitMessage {
        String source = argument.getString();
        for (DTRBitmask bitmaskType : DTRBitmask.values()) {
            if (source.equalsIgnoreCase(bitmaskType.getName())) {
                return (bitmaskType);
            }
        }

        throw new BladeExitMessage("No bitmask type with the name " + source + " found.");
    }

    @NotNull
    public List<String> suggest(@NotNull BladeContext context, @NotNull BladeArgument argument) throws BladeExitMessage {
        List<String> completions = new ArrayList<>();
        String source = argument.getString();

        for (DTRBitmask bitmask : DTRBitmask.values()) {
            if (StringUtils.startsWithIgnoreCase(bitmask.getName(), source)) {
                completions.add(bitmask.getName());
            }
        }

        return (completions);
    }
}
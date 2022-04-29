package net.frozenorb.foxtrot.util.provider;

import me.vaperion.blade.argument.BladeArgument;
import me.vaperion.blade.argument.BladeProvider;
import me.vaperion.blade.context.BladeContext;
import me.vaperion.blade.exception.BladeExitMessage;
import net.frozenorb.foxtrot.util.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IntegerProvider implements BladeProvider<Integer> {

    @Override
    public @Nullable Integer provide(@NotNull BladeContext context, @NotNull BladeArgument argument) throws BladeExitMessage {
        String source = argument.getString();
        int number = Integer.parseInt(source);

        if (!NumberUtils.isInteger(source)){
            throw new BladeExitMessage("Error: That isn't a valid number.");
        }

        if (number > 1000000000){
            throw new BladeExitMessage("Error: That isn't a valid number.");
        }

        return number;
    }
}

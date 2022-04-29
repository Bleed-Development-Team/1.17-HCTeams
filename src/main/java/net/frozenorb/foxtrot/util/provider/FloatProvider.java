package net.frozenorb.foxtrot.util.provider;

import me.vaperion.blade.argument.BladeArgument;
import me.vaperion.blade.argument.BladeProvider;
import me.vaperion.blade.context.BladeContext;
import me.vaperion.blade.exception.BladeExitMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FloatProvider implements BladeProvider<Float> {

    @Override
    public @Nullable Float provide(@NotNull BladeContext context, @NotNull BladeArgument argument) throws BladeExitMessage {
        String source = argument.getString();
        float input = Float.parseFloat(source);

        if (Float.isNaN(input)){
            throw new BladeExitMessage("Error: The input provided is not valid.");
        }

        if (input > 100000000000000000000000000000000000000F){
            throw new BladeExitMessage("Error: That isn't a valid float.");
        }

        return input;
    }

}

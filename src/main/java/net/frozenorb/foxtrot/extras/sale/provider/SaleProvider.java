package net.frozenorb.foxtrot.extras.sale.provider;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.sale.Sale;
import org.bukkit.entity.Player;

public class SaleProvider implements ContextResolver<Sale, BukkitCommandExecutionContext> {
    @Override
    public Sale getContext(BukkitCommandExecutionContext event) throws InvalidCommandArgument {
        String source = event.popFirstArg();

        Sale sale = Foxtrot.getInstance().getSaleManager().getSale(source);

        if (sale == null) {
            throw new InvalidCommandArgument("Sale '" + source + "' does not exist.");
        }

        return (sale);
    }
}

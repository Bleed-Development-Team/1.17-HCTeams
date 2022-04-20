package net.frozenorb.foxtrot.util.providers;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import net.frozenorb.foxtrot.team.claims.Subclaim;

public class SubclaimProvider implements ContextResolver<Subclaim, BukkitCommandExecutionContext> {
    @Override
    public Subclaim getContext(BukkitCommandExecutionContext bukkitCommandExecutionContext) throws InvalidCommandArgument {
        return null;
    }
}

package net.frozenorb.foxtrot.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public interface ParameterType<T> {
    T transform(CommandSender paramCommandSender, String paramString);

    List<String> tabComplete(Player paramPlayer, Set<String> paramSet, String paramString);
}

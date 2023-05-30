package net.frozenorb.foxtrot.provider.nametags;

import org.bukkit.entity.*;

public interface NametagAdapter {
    String getAndUpdate(Player from, Player to);
}
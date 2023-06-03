package net.frozenorb.foxtrot.provider.scoreboard;

import org.bukkit.entity.Player;

import java.util.LinkedList;

public interface ScoreboardAdapter {

    String getTitle(Player player);
    LinkedList<String> getLines(Player player);

}

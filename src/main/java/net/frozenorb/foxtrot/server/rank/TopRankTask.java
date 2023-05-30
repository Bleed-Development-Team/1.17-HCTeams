package net.frozenorb.foxtrot.server.rank;

import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TopRankTask implements Runnable {

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        List<String> ranks = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {

        }

        if (ranks.isEmpty()) return;

        Bukkit.broadcastMessage(CC.translate("&7&m----------------------------------"));
        Bukkit.broadcastMessage(CC.translate("&6&lOnline Donator Ranks"));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&r" + builder.substring(0, builder.toString().length() - 2)));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&7&m----------------------------------"));
    }
}

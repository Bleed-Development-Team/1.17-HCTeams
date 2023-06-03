package net.frozenorb.foxtrot.provider.scoreboard;

import lombok.Getter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.provider.scoreboard.listener.ScoreboardListener;
import net.frozenorb.foxtrot.provider.scoreboard.thread.ScoreboardThread;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hyrical.fastboard.FastBoard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardHandler {

    @Getter private ScoreboardAdapter adapter;
    @Getter private Map<UUID, HCFScoreboard> boards = new HashMap<>();

    public ScoreboardHandler(ScoreboardAdapter adapter){
        this.adapter = adapter;

        new ScoreboardThread().start();
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), HCF.getInstance());
    }

    public void create(Player player){
        boards.put(player.getUniqueId(), new HCFScoreboard(player, new FastBoard(player)));
    }

    public void delete(Player player){
        boards.remove(player.getUniqueId());
    }

}

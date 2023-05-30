package net.frozenorb.foxtrot.chat.trivia.listener;

import net.frozenorb.foxtrot.HCF;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;

public class TriviaListener implements Listener {

    @EventHandler
    public void chat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!HCF.getInstance().getTriviaHandler().isActive()) return;
        if (!(Objects.equals(HCF.getInstance().getTriviaHandler().currentAnswer.toLowerCase(), message.toLowerCase()))) return;

        HCF.getInstance().getTriviaHandler().endTrivia(player);
        event.setCancelled(true);
    }
}

package net.frozenorb.foxtrot.chat.chatgames.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatGamesListener implements Listener {


    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        if (Foxtrot.getInstance().getChatGamesHandler().isGoingOn()){
            if (Foxtrot.getInstance().getChatGamesHandler().isCorrect(event.getMessage())){
                event.setCancelled(true);

                Foxtrot.getInstance().getChatGamesHandler().endGame(false, event.getPlayer());
            }
        }
    }
}

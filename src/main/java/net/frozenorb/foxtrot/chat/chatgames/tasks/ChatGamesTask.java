package net.frozenorb.foxtrot.chat.chatgames.tasks;

import net.frozenorb.foxtrot.Foxtrot;

public class ChatGamesTask implements Runnable {

    @Override
    public void run() {
        Foxtrot.getInstance().getChatGamesHandler().startNewGame();
    }
}

package net.frozenorb.foxtrot.provider.scoreboard.thread;

import net.frozenorb.foxtrot.HCF;

public class ScoreboardThread extends Thread {

    @Override
    public void run() {
        while (true){
            try {
                sleep(100L);
                tick();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void tick(){
        HCF.getInstance().getScoreboardHandler().getBoards().forEach((key, value) -> { value.updateBoard(); });
    }
}

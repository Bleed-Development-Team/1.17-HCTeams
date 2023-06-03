package net.frozenorb.foxtrot.provider.scoreboard;

import lombok.Getter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;
import org.hyrical.fastboard.FastBoard;

import java.util.List;

public class HCFScoreboard {

    @Getter private Player player;
    @Getter private FastBoard board;

    public HCFScoreboard(Player player, FastBoard board){
        this.player = player;
        this.board = board;
    }

    public void updateBoard(){
        String title = CC.translate(HCF.getInstance().getScoreboardHandler().getAdapter().getTitle(player));
        List<String> boardLines = CC.translate(HCF.getInstance().getScoreboardHandler().getAdapter().getLines(player));

        if (boardLines.isEmpty() && !board.isDeleted()){
            board.delete();
            return;
        }

        if (board.isDeleted()) this.board = new FastBoard(player);

        board.updateTitle(title);
        board.updateLines(boardLines);
    }

}

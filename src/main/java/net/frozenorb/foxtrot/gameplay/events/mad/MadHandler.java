package net.frozenorb.foxtrot.gameplay.events.mad;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.gameplay.events.mad.game.MadGame;
import org.bukkit.ChatColor;

public class MadHandler {

    public static final String PREFIX = ChatColor.RED + "[Mad]";

    public static final int POINTS_DEATH_PENALTY = 1;
    public static final String KOTH_NAME_PREFIX = "Mad";
    public static final int TIME_TO_CAP = 60;

    @Getter @Setter private MadGame game;

    public static int getCapsToWin() {
        return 5;
    }
}
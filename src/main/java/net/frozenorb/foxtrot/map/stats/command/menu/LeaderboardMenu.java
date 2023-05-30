package net.frozenorb.foxtrot.map.stats.command.menu;

import io.github.nosequel.menu.Menu;
import net.frozenorb.foxtrot.map.stats.command.StatsTopCommand;
import net.frozenorb.foxtrot.map.stats.command.menu.button.LeaderboardButton;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LeaderboardMenu extends Menu {

    public LeaderboardMenu(Player player){
        super(player, CC.translate("&5Leaderboards"), 9 * 3);
    }

    @Override
    public void tick() {
        buttons[10] = new LeaderboardButton(Material.ORANGE_WOOL, StatsTopCommand.StatsObjective.KILLS);
        buttons[12] = new LeaderboardButton(Material.RED_WOOL, StatsTopCommand.StatsObjective.DEATHS);
        buttons[14] = new LeaderboardButton(Material.GREEN_WOOL, StatsTopCommand.StatsObjective.TEAM);
        buttons[16] = new LeaderboardButton(Material.PURPLE_WOOL, StatsTopCommand.StatsObjective.HIGHEST_KILLSTREAK);
    }

}

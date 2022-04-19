package net.frozenorb.foxtrot.team.menu;

import io.github.nosequel.menu.Menu;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;

public class TeamManageMenu extends Menu {

    private Team team;

    public TeamManageMenu(Player player, Team team) {
        super(player, "Team Manage", 9);

        this.team = team;
    }

    @Override
    public void tick(){

    }



}

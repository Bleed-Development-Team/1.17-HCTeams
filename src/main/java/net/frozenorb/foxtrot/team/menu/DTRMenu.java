package net.frozenorb.foxtrot.team.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.lib.menu.Button;
import net.frozenorb.foxtrot.lib.menu.Menu;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.button.DTRButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class DTRMenu extends Menu {

    Team team;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (i == 3) {
                buttons.put(i, new DTRButton(team, false));

            } else if (i == 5) {
                buttons.put(i, new DTRButton(team, true));

            } else {
                buttons.put(i, Button.placeholder(Material.BLACK_STAINED_GLASS_PANE));
            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Manage DTR";
    }
}
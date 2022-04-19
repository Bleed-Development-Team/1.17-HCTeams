package net.frozenorb.foxtrot.team.menu;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.lib.menu.Button;
import net.frozenorb.foxtrot.lib.menu.Menu;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.button.KickPlayerButton;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
public class KickPlayersMenu extends Menu {

    @NonNull @Getter Team team;

    @Override
    public String getTitle(Player player) {
        return "Players in " + team.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        ArrayList<UUID> uuids = new ArrayList<>();
        uuids.addAll(team.getMembers());

        Collections.sort(uuids, Comparator.comparing(u -> UUIDUtils.name(u).toLowerCase()));

        for (UUID uuid : uuids) {
            buttons.put(index, new KickPlayerButton(uuid, team));
            index++;
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}

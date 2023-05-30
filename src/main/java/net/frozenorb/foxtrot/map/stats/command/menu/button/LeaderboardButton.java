package net.frozenorb.foxtrot.map.stats.command.menu.button;

import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.map.stats.StatsEntry;
import net.frozenorb.foxtrot.map.stats.StatsHandler;
import net.frozenorb.foxtrot.map.stats.command.StatsTopCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamCommands;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Map;

public class LeaderboardButton extends Button {
    private final StatsTopCommand.StatsObjective obj;

    private StatsHandler statsHandler;

    public LeaderboardButton(Material material, StatsTopCommand.StatsObjective obj) {
        super(ItemBuilder.of(material)
                .name(CC.translate(obj.getName()))
                .build());

        this.obj = obj;
        this.statsHandler = HCF.getInstance().getMapHandler().getStatsHandler();
    }

    @Override
    public String getDisplayName() {
        return obj.getName();
    }

    @Override
    public String[] getLore() {
        ArrayList<String> lore = new ArrayList<>();

        lore.add("&7&m------------------------------------------");

        int index = 0;

        if (obj == StatsTopCommand.StatsObjective.TEAM){
            for (Map.Entry<Team, Integer> entry : TeamCommands.getSortedTeams().entrySet()){ //Team -> Points
                if (entry == null) continue;

                index++;

                lore.add("&7" + index + ". " + obj.getColor() + entry.getKey().getName() + " &f[" + entry.getValue() + "]");
            }
        } else {
            for (Map.Entry<StatsEntry, String> entry : statsHandler.getLeaderboards(obj, 10).entrySet()) {
                if (entry == null) continue;

                index++;

                lore.add("&7" + index + ". " + obj.getColor() + UUIDUtils.name(entry.getKey().getOwner()) + " &f[" + entry.getValue() + "]");
            }
        }


        lore.add("&7&m------------------------------------------");

        String[] str = new String[lore.size()];

        for (int i = 0; i < lore.size(); i++) {
            str[i] = lore.get(i);
        }

        return str;
    }
}

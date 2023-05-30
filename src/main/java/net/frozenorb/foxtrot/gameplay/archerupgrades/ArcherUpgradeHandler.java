package net.frozenorb.foxtrot.gameplay.archerupgrades;

import net.frozenorb.foxtrot.gameplay.archerupgrades.type.Fainter;
import net.frozenorb.foxtrot.gameplay.archerupgrades.type.Medusa;
import net.frozenorb.foxtrot.gameplay.archerupgrades.type.Phantom;
import net.frozenorb.foxtrot.gameplay.archerupgrades.type.Venom;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArcherUpgradeHandler {

    public List<ArcherUpgrade> archerUpgrades = new ArrayList<>();

    public ArcherUpgradeHandler(){
        archerUpgrades.add(new Medusa());
        archerUpgrades.add(new Fainter());
        archerUpgrades.add(new Phantom());
        archerUpgrades.add(new Venom());
    }

    public ArcherUpgrade getCurrentArcher(Player player){
        for (ArcherUpgrade archerUpgrade : archerUpgrades){
            if (archerUpgrade.isWearing(player)){
                return archerUpgrade;
            }
        }
        return null;
    }

}

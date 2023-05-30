package net.frozenorb.foxtrot.gameplay.archerupgrades.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import net.frozenorb.foxtrot.gameplay.archerupgrades.menu.ArcherUpgradeMenu;
import org.bukkit.entity.Player;

public class ArcherUpgradesCommand extends BaseCommand {

    @CommandAlias("archerupgrades")
    public void archerUpgrades(Player player){
        new ArcherUpgradeMenu(player).updateMenu();
    }
}

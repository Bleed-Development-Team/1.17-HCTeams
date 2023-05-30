package net.frozenorb.foxtrot.gameplay.armorclass.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassMenu;
import org.bukkit.entity.Player;

public class ArmorClassCommand extends BaseCommand {

    @CommandAlias("armorclass|classes|armorclasses")
    public void armorClasses(Player player){
        new ArmorClassMenu(player).updateMenu();
    }
}

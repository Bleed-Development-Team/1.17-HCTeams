package net.frozenorb.foxtrot.gameplay.ability.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import net.frozenorb.foxtrot.gameplay.ability.menu.AbilitiesMenu;
import org.bukkit.entity.Player;

@CommandAlias("abilities")
public class AbiltiesCommand extends BaseCommand {

    @Default
    @HelpCommand
    public void onCmd(Player player){
        new AbilitiesMenu(player).updateMenu();
    }
}

package net.frozenorb.foxtrot.extras.settings.commands;

import me.vaperion.blade.annotation.Command;
import net.frozenorb.foxtrot.extras.settings.menu.SettingsMenu;
import org.bukkit.entity.Player;

public class SettingsCommand {

    @Command(value = {"settings", "setting"})
    public static void settingCommand(Player sender) {
        new SettingsMenu().openMenu(sender);
    }
}
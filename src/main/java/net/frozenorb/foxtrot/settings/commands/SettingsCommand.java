package net.frozenorb.foxtrot.settings.commands;

import me.vaperion.blade.annotation.Command;
import net.frozenorb.foxtrot.settings.menu.SettingsMenu;
import org.bukkit.entity.Player;

public class SettingsCommand {

    @Command(value = {"settings", "setting"})
    public void settingCommand(Player sender) {
        new SettingsMenu().openMenu(sender);
    }
}
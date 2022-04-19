package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;

public class TeamHelpCommand {

    @Command(value={ "team help", "t help", "f help", "faction help", "fac help" })
    public static void teamHelp(@Sender Player player) {
        TeamCommand.team(player);
    }

}
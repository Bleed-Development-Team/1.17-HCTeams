package net.frozenorb.foxtrot.team.commands.team;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Optional;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.command.CommandSender;

public class TeamJSONCommand {

    @Command(value={ "team json", "t json", "f json", "faction json", "fac json" })
    @Permission(value = "op")
    public static void teamJSON(@Sender CommandSender sender, @Optional("team") Team team) {
        sender.sendMessage(team.toJSON().toString());
    }

}
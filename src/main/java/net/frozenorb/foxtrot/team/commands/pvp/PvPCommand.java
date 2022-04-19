package net.frozenorb.foxtrot.team.commands.pvp;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Sender;
import org.bukkit.entity.Player;

public class PvPCommand {

    @Command(value ={ "pvp", "timer", "pvptimer" })
    public static void pvp(@Sender Player sender) {
        String[] msges = {
                "§c/pvp lives [target] - Shows amount of lives that a player has",
                "§c/pvp revive <player> - Revives targeted player",
                "§c/pvp time - Shows time left on PVP Timer",
                "§c/pvp enable - Remove PVP Timer"};

        sender.sendMessage(msges);
    }

}
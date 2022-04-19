package net.frozenorb.foxtrot.team.commands.team.chatspy;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Permission;
import net.frozenorb.foxtrot.Foxtrot;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeamChatSpyClearCommand {

    @Command(value={ "team chatspy clear", "t chatspy clear", "f chatspy clear", "faction chatspy clear", "fac chatspy clear" })
    @Permission(value = "foxtrot.chatspy")
    public static void teamChatSpyClear(Player sender) {
        Foxtrot.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), new ArrayList<ObjectId>());
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on any teams.");
    }

}
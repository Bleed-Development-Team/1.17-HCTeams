package net.frozenorb.foxtrot.team.commands.team;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import org.bukkit.entity.Player;

@CommandAlias("gc|pc")
public class TeamChatTwo extends BaseCommand {

    @Default
    public static void gc(Player sender) {
        TeamChatCommand.setChat(sender, ChatMode.PUBLIC);
    }
}

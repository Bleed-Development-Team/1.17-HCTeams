package net.frozenorb.foxtrot.team.commands.team;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import org.bukkit.entity.Player;

@CommandAlias("oc")
public class TeamChatThree extends BaseCommand {
    @Default
    public static void oc(Player sender) {
        TeamChatCommand.setChat(sender, ChatMode.OFFICER);
    }
}

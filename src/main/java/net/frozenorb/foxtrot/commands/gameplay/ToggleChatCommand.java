package net.frozenorb.foxtrot.commands.gameplay;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.HCF;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("ToggleChat|ToggleGlobalChat|TGC")
public class ToggleChatCommand extends BaseCommand {

    @Default
    public static void toggleChat(Player sender) {
        boolean val = !HCF.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see global chat!");
        HCF.getInstance().getToggleGlobalChatMap().setGlobalChatToggled(sender.getUniqueId(), val);
    }

}
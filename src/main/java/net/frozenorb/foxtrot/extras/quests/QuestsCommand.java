package net.frozenorb.foxtrot.extras.quests;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("quests|quest")
public class QuestsCommand extends BaseCommand {
    @Default
    public void onQuestCommand(Player sender) {
        if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
            sender.sendMessage(CC.translate("&cYou must be in a safe zone to use this command."));
            return;
        }
        sender.sendMessage(CC.translate("&cComing soon..."));
    }
}

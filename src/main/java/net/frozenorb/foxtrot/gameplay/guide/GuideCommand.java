package net.frozenorb.foxtrot.gameplay.guide;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.gameplay.guide.menus.GuideMenu;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("guide")
public class GuideCommand extends BaseCommand {
    @Default
    public void onGuideCommand(Player sender) {
        if (sender.hasPermission("op")) {
            new GuideMenu(sender).updateMenu();
            return;
        }
        if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
            sender.sendMessage(CC.translate("&cYou must be in a safe zone to use this command."));
            return;
        }
        sender.sendMessage(CC.translate("&cComing soon..."));
    }
}

package net.frozenorb.foxtrot.server.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

public class ReloadCommand extends BaseCommand {

    @CommandAlias("hcfreload")
    @CommandPermission("op")
    public void reload(Player player){
        HCF.getInstance().reloadConfig();
        HCF.getInstance().saveConfig();

        player.sendMessage(CC.translate("&aReloaded HCF."));
    }
}

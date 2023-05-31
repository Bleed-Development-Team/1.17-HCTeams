package net.frozenorb.foxtrot.commands.op;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Hidden;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@CommandAlias("trolling")
@CommandPermission("op")
@Hidden
public class TrollingCommand extends BaseCommand {

    @Subcommand("nocooldowns")
    public void noCooldowns(Player player){
        if (player.hasMetadata("nocds")){
            player.removeMetadata("nocds", HCF.getInstance());
        } else {
            player.setMetadata("nocds", new FixedMetadataValue(HCF.getInstance(), "nocds"));
        }

        player.sendMessage(CC.translate("&fYou have toggled the trolling thing " + (player.hasMetadata("nocds") ? "&aon" : "&coff") + "&f."));
    }
}

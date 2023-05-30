package net.frozenorb.foxtrot.gameplay.ability.partnerpackages.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import org.bukkit.entity.Player;

@CommandAlias("partnerpackage")
@CommandPermission("op")
public class PartnerPackageCommand extends BaseCommand {

    @Subcommand("give")
    public void givePP(Player player, @Default Integer argAmount){
        int amount = (argAmount == null ? 0 : argAmount);

        for (int i = 0; i < amount; i++){
            player.getInventory().addItem(HCF.getInstance().getPartnerPackageHandler().PACKAGE_ITEM);
        }
    }
}

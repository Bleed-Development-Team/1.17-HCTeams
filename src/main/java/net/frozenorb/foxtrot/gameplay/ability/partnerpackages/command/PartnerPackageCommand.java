package net.frozenorb.foxtrot.gameplay.ability.partnerpackages.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("partnerpackage")
@CommandPermission("op")
public class PartnerPackageCommand extends BaseCommand {

    @Subcommand("give")
    public void givePP(CommandSender player, @Flags("other") @Name("player") Player target, @Flags("other") @Default Integer argAmount){
        int amount = (argAmount == null ? 0 : argAmount);

        target.getInventory().addItem(ItemBuilder.copyOf(HCF.getInstance().getPartnerPackageHandler().PACKAGE_ITEM).amount(amount).build());
    }
}

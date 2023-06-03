package net.frozenorb.foxtrot.commands.op.gems;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

@CommandAlias("gems")
public class GemsCommand extends BaseCommand {

    @Default
    public void def(Player player2, @Flags("other") @Optional Player target){
        Player player = (target == null ? player2 : target);
        player2.sendMessage(CC.translate("&fGems: &2" + NumberFormat.getInstance().format(HCF.getInstance().getGemsMap().getGems(player.getUniqueId()))));
    }

    @Subcommand("give")
    @CommandPermission("gems.give")
    public void gemsGive(Player player, @Flags("other") @Name("player") Player target, @Flags("other") @Name("amount") int amount){
        HCF.getInstance().getGemsMap().addGems(target.getUniqueId(), amount);
        player.sendMessage(CC.translate("&aUpdated gems."));
    }

    @Subcommand("set")
    @CommandPermission("gems.set")
    public void gemsSet(Player player, @Flags("other") @Name("player") Player target, @Flags("other") @Name("amount") int amount){
        HCF.getInstance().getGemsMap().setGems(player.getUniqueId(), amount);
        player.sendMessage(CC.translate("&aUpdated gems."));
    }

    @Subcommand("remove")
    @CommandPermission("gems.remove")
    public void gemsRemove(Player player, @Flags("other") @Name("player") Player target, @Flags("other") @Name("amount") int amount){
        HCF.getInstance().getGemsMap().removeGems(player.getUniqueId(), amount);
        player.sendMessage(CC.translate("&aUpdated gems."));
    }
}

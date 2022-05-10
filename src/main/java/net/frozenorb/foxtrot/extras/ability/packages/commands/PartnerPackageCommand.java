package net.frozenorb.foxtrot.extras.ability.packages.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("partnerpackage")
@CommandPermission("op")
public class PartnerPackageCommand extends BaseCommand {

    @Default
    @HelpCommand
    public void onHelpDefault(Player main, @Optional @Flags("other") Player target) {
        Player player = target == null ? main : target;

        player.getInventory().addItem(Foxtrot.getInstance().getAbilityPackage().getPackage());
        player.sendMessage(CC.translate("&6&lFox &7| &fYou have received an &d&lAbility Package&f."));
    }

    @Subcommand("all")
    public void onSubCommandAll(Player sender) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getInventory().addItem(Foxtrot.getInstance().getAbilityPackage().getPackage());
            player.sendMessage(CC.translate("&6&lFox &7| &fYou have received an &d&lAbility Package&f."));
        });
    }
}

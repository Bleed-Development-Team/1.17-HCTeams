package net.frozenorb.foxtrot.extras.ability.packages.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("partnerpackage")
@CommandPermission("op")
public class PartnerPackageCommand extends BaseCommand {
    @Default
    @HelpCommand
    public void onHelpDefault(Player player) {
        player.getInventory().addItem(Foxtrot.getInstance().getAbilityPackage().getPackage());
    }
    @Subcommand("all")
    public void onSubCommandAll(Player sender) {
        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().addItem(Foxtrot.getInstance().getAbilityPackage().getPackage()));
    }
}

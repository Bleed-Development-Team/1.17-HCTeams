package net.frozenorb.foxtrot.extras.ability.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("ability")
@CommandPermission("foxtrot.ability")
public class AbilityCommand extends BaseCommand {

    @Subcommand("give")
    public void onAbilityCommand(Player player) {
        player.getInventory().addItem(Items.getBoneAbility());
        player.getInventory().addItem(Items.getMedkit());
        player.getInventory().addItem(Items.getPowerstone());
        player.getInventory().addItem(Items.getComboAbility());
        player.getInventory().addItem(Items.getSnowball());
        player.getInventory().addItem(Items.getPotionCounter());
        player.getInventory().addItem(Items.getAntiPearl());

    }
}

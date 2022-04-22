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
    public void onAbilityCommand(Player player, @Name(value = "player") Player target, @Name(value = "ability") String ability) {
        switch (ability.toLowerCase()) {
            case "powerstone" -> {
                player.getInventory().addItem(Items.getPowerstone());
                player.sendMessage(CC.translate("&aYou gave " + target.getName() + " a powerstone."));
            }
            case "bone" -> {
                player.getInventory().addItem(Items.getBoneAbility());
                player.sendMessage(CC.translate("&aYou gave " + target.getName() + " a bone."));
            }
        }
    }
}

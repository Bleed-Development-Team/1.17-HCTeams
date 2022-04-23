package net.frozenorb.foxtrot.extras.ability.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("ability")
@CommandPermission("foxtrot.ability")
public class AbilityCommand extends BaseCommand {

    @Subcommand("give")
    public void onAbilityCommand(Player player) {
        for (Ability ability : Foxtrot.getInstance().getAbilityHandler().getAbilities()){
            player.getInventory().addItem(ability.getItemStack());
        }
    }

    @Subcommand("reset")
    public void reset(Player player){
        player.sendMessage(CC.translate("&csadly, this does not work yet :("));
    }
}

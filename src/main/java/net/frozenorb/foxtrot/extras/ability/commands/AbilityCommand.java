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
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.entity.Player;

@CommandAlias("ability")
@CommandPermission("foxtrot.ability")
public class AbilityCommand extends BaseCommand {

    @Subcommand("giveall")
    public void onAbilityCommand(Player player) {
        for (Ability ability : Foxtrot.getInstance().getAbilityHandler().getAbilities()){
            player.getInventory().addItem(ability.getItemStack());
        }
    }

    @Subcommand("reset")
    public void reset(Player player){
        int i = 0;
        for (Ability ability : Foxtrot.getInstance().getAbilityHandler().getAbilities()){
            if (Cooldown.isOnCooldown(ability.getCooldownID(), player)){
                Cooldown.removeCooldown(ability.getCooldownID(), player);
                i++;
            }
        }

        Cooldown.removeCooldown("partner", player);
        player.sendMessage(CC.translate("&aYou have reset &e" + i + " &aability cooldowns."));
    }
}

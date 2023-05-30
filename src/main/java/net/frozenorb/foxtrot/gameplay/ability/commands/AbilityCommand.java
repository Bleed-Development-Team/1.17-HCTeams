package net.frozenorb.foxtrot.gameplay.ability.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.entity.Player;

@CommandAlias("ability")
@CommandPermission("foxtrot.ability")
public class AbilityCommand extends BaseCommand {

    @Subcommand("giveall")
    @CommandPermission("foxtrot.ability")
    public void onAbilityCommand(Player player) {
        for (Ability ability : HCF.getInstance().getAbilityHandler().getAllAbilities()){
            player.getInventory().addItem(ability.getItemStack());
        }
    }

    @Subcommand("reset")
    @CommandPermission("foxtrot.ability")
    public void reset(Player player){
        int i = 0;
        for (Ability ability : HCF.getInstance().getAbilityHandler().getAllAbilities()){
            if (Cooldown.isOnCooldown(ability.getID(), player)){
                Cooldown.removeCooldown(ability.getID(), player);
                i++;
            }
        }

        player.sendMessage(CC.translate("&aYou have reset &e" + i + " &aability cooldowns."));
    }
}

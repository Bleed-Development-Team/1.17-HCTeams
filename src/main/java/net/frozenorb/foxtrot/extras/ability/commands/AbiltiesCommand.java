package net.frozenorb.foxtrot.extras.ability.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("abilities")
public class AbiltiesCommand extends BaseCommand {

    @Default
    public void onCmd(Player player){
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&c&lAbilities &7| &fList"));
        player.sendMessage("");
        for (Ability ability : Foxtrot.getInstance().getAbilityHandler().getAbilities()) {
            player.sendMessage(CC.translate(ability.getName()));
        }
        player.sendMessage(CC.translate(""));
    }
}

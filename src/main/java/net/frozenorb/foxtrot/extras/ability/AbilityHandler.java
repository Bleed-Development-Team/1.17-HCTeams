package net.frozenorb.foxtrot.extras.ability;

import net.frozenorb.foxtrot.extras.ability.item.Items;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

public class AbilityHandler {

    public AbilityHandler(){

    }

    public void giveAbility(Player player, Player target, String ability){
        if (!isAbilityItem(ability)){
            player.sendMessage(CC.translate("&cNo ability with the name " + ability + " found."));
            return;
        }

        switch (ability.toLowerCase()){
            case "bone":
                target.getInventory().addItem(Items.getBone());
                player.sendMessage(CC.translate("&aYou have given " + target.getName() + " the exotic bone."));
                break;
        }
    }

    private boolean isAbilityItem(String ability){
        return ability.equalsIgnoreCase("bone") || ability.equalsIgnoreCase("debuff");
    }
}

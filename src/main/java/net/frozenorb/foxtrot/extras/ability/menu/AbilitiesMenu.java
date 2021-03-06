package net.frozenorb.foxtrot.extras.ability.menu;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.AbilityHandler;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

public class AbilitiesMenu extends Menu {

    public AbilitiesMenu(Player player) {
        super(player, CC.translate("Abilities"), 27);
    }


    @Override
    public void tick() {
        Player player = getPlayer();
        int i = 0;

        for (Ability ability : Foxtrot.getInstance().getAbilityHandler().getAbilities()) {
            if (player.hasPermission("op")){
                buttons[i++] = new Button(ability.getItemStack()).setClickAction(event -> {
                    player.getInventory().addItem(ability.getItemStack());
                    event.setCancelled(true);
                });
            } else {
                buttons[i++] = new Button(ability.getItemStack());
            }
        }
    }
}

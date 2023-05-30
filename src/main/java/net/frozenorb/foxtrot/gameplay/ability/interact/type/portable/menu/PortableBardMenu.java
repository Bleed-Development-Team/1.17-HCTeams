package net.frozenorb.foxtrot.gameplay.ability.interact.type.portable.menu;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.interact.type.portable.PortableBardEffect;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.entity.Player;

public class PortableBardMenu extends Menu {

    public PortableBardMenu(Player player) {
        super(player, "Portable Effects", 9);
    }

    @Override
    public void tick() {
        Player player = getPlayer();

        int i = 0;
        for (Ability ability : HCF.getInstance().getAbilityHandler().getAllAbilities()){
            if (!(ability instanceof PortableBardEffect)) continue;

            buttons[i] = new Button(ability.getItemStack())
                    .setLore(new String[]{"&f", "&e| &fClick here to give yourself", "&e| &fthe " + ability.getName() + " &feffect."})
                    .setClickAction(event -> {
                        event.setCancelled(true);
                        if (player.getItemInHand().getAmount() == 1){
                            player.setItemInHand(null);
                            player.closeInventory();
                        } else {
                            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                        }

                        player.getInventory().addItem(ItemBuilder.copyOf(ability.getItemStack()).amount(3).build());
                    });
            i += 2;
        }
    }
}

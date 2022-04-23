package net.frozenorb.foxtrot.extras.ability.impl.portable.menu;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PortableMenu extends Menu {

    public PortableMenu(Player player) {
      super(player, "Portable Menu", 9);
   }

    @Override
    public void tick() {
        this.buttons[0] = new Button(Items.getStrength()).setClickAction(event -> choose(event, "strength"));
        this.buttons[1] = new Button(Items.getResistance()).setClickAction(event -> choose(event, "resistance"));
        this.buttons[2] = new Button(Items.getRegen()).setClickAction(event -> choose(event, "regen"));
        this.buttons[3] = new Button(Items.getInvis()).setClickAction(event -> choose(event, "invis"));
        this.buttons[4] = new Button(Items.getJump()).setClickAction(event -> choose(event, "jump"));
    }

    private void choose(InventoryClickEvent event, String item){
        Player player = getPlayer();
        event.setCancelled(true);

        switch (item) {
            case "strength" -> {
                remove();
                player.getInventory().addItem(Items.getStrength());
            }
            case "resistance" -> {
                remove();
                player.getInventory().addItem(Items.getResistance());
            }
            case "regen" -> {
                remove();
                player.getInventory().addItem(Items.getRegen());
            }
            case "jump" -> {
                remove();
                player.getInventory().addItem(Items.getJump());
            }
            case "invis" -> {
                remove();
                player.getInventory().addItem(Items.getInvis());
            }
            default -> {
                player.sendMessage(CC.translate("&cAn error has occurred."));
            }
        }
    }

    private void remove(){
        Player player = getPlayer();

        if (player.getItemInHand().getAmount() > 1) {
            int amount = player.getItemInHand().getAmount() - 1;
            player.getItemInHand().setAmount(amount);
        } else {
            player.setItemInHand(null);
        }
    }
}

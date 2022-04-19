package net.frozenorb.foxtrot.team.menu;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.menu.button.BooleanButton;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Callback;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ConfirmMenu extends Menu {
    private String title;
    private Callback<Boolean> response;


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, response));

            } else if (i == 5) {
                buttons.put(i, new BooleanButton(false, response));

            } else {
                buttons.put(i, Button.placeholder(Material.BLACK_STAINED_GLASS_PANE));

            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return title;
    }

    /**
     * The method to get the buttons for the current inventory tick
     * <p>
     * Use {@code this.buttons[index] = Button} to assign
     * a button to a slot.
     */
    @Override
    public void tick() {
        buttons[3] = new Button(ItemBuilder.of(Material.GREEN_WOOL).name(CC.translate("&aYes")).build()).setClickAction(a -> response.callback(true));
    }
}

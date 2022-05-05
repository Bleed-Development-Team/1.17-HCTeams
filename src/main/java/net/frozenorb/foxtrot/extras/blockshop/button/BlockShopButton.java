package net.frozenorb.foxtrot.extras.blockshop.button;

import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class BlockShopButton extends Button {

    public BlockShopButton instance;

    private final Player player;
    private final Material material;
    private final String name;
    private final int amount;
    private final double price;

    public BlockShopButton(Player player, Material material, String name, int amount, double price) {
        super(material);

        this.player = player;
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.price = price;

        this.instance = this;
    }

    @Override
    public String getDisplayName() {
        return CC.translate(("&b" + name + " &7- &f" + amount));
    }

    @Override
    public String[] getLore() {

        return new String[]{
            "§bYour balance§7: §a$" + NumberFormat.getNumberInstance(Locale.US).format(FrozenEconomyHandler.getBalance(player.getUniqueId())),
            "§bPrice&7: §f" + NumberFormat.getNumberInstance(Locale.US).format(price),
            "§r",
            "§aClick to purchase."
        };
    }

}

package net.frozenorb.foxtrot.extras.sale.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.sale.Sale;
import net.frozenorb.foxtrot.extras.sale.gui.ActiveSalesGUI;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.DyeUtils;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("sale|sales")
public class SalesCommand extends BaseCommand {

    @Default
    @CatchUnknown
    public void onDefault(Player sender) {
        new ActiveSalesGUI(sender).updateMenu();
        sender.sendMessage(CC.translate("&eOpening active sales menu..."));
    }

    @CommandAlias("create")
    @CommandPermission("op")
    public void onCreate(Player sender, @Name("saleName") String name, @Name("length") String length) {
        sender.sendMessage(CC.translate("&eCreating sale &6" + name + "&e..."));
        sender.sendMessage(CC.translate("&eSale &6" + name + "&e created!"));
        sender.sendMessage(CC.translate("&eTo edit this sale, use &6/sale edit " + name + "&e."));

        Foxtrot.getInstance().getSaleManager().create(name, name, TimeUtils.parseTime(length), " ", ChatColor.WHITE);
    }

    @CommandAlias("edit")
    @CommandPermission("op")
    public void onEdit(Player sender, @Name("saleId") Sale sale, @Name("module") String module, @Name("value") String value) {
        switch (module) {
            case "name" -> sale.setName(value);
            case "description" -> sale.setDescription(value);
            case "color" -> sale.setColor(DyeUtils.stringToChatColor(value));
        }
        sender.sendMessage(CC.translate("&eSale &6" + sale.getDisplayName() + "&e edited!"));
    }

}

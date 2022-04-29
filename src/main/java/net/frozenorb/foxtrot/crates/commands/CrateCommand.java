package net.frozenorb.foxtrot.crates.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.crates.Crate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

@CommandAlias("hctcrate")
@CommandPermission("op")
public class CrateCommand extends BaseCommand {


    @Default
    public static void onGive(Player sender, @Name("kit") String kit) {
        ItemStack enderChest = new ItemStack(Material.ENDER_CHEST, 1);
        ItemMeta itemMeta = enderChest.getItemMeta();

        try {
            Crate crate = Foxtrot.getInstance().getCrateHandler().getCrates().get(kit.toLowerCase());

            itemMeta.setDisplayName(crate.getKitName());
            itemMeta.setLore(crate.getLore());
            enderChest.setItemMeta(itemMeta);

            sender.getInventory().addItem(enderChest);
            sender.sendMessage(GREEN + "Generated the " + crate.getKitName() + GREEN + " crate and added it to your inventory!");
        } catch (Exception ex) {
            sender.sendMessage(RED + "Cannot create crate item for kit '" + kit + "'");
        }
    }
    

    @Subcommand("give")
    public static void onGive(Player sender, @Name("kit") String kit, @Flags("other") @Name("target") Player target) {
        ItemStack enderChest = new ItemStack(Material.ENDER_CHEST, 1);
        ItemMeta itemMeta = enderChest.getItemMeta();

        try {
            Crate crate = Foxtrot.getInstance().getCrateHandler().getCrates().get(kit.toLowerCase());

            assert itemMeta != null;
            itemMeta.setDisplayName(crate.getKitName());
            itemMeta.setLore(crate.getLore());
            enderChest.setItemMeta(itemMeta);

            target.getInventory().addItem(enderChest);
            sender.sendMessage(GREEN + "Generated the " + crate.getKitName() + GREEN + " crate and added it to " + target.getName() +"'s inventory!");
        } catch (Exception ex) {
            sender.sendMessage(RED + "Cannot create crate item for kit '" + kit + "'");
        }
    }


    @Subcommand("create")
    public static void onCreate(Player player, @Name("kit") String kit) {
        Crate crate = new Crate(kit);

        Foxtrot.getInstance().getCrateHandler().getCrates().put(kit.toLowerCase(), crate);
        player.sendMessage(GREEN + "Created an empty crate for kit `" + crate.getKitName() + "`");
    }

    @Subcommand("edit")
    public static void onEdit(Player player, @Name("kit") String kit) {
        Crate crate = Foxtrot.getInstance().getCrateHandler().getCrates().get(kit.toLowerCase());

        if (crate == null) {
            player.sendMessage(RED + "Cannot edit crate for kit `" + kit + "`");
            return;
        }

        Foxtrot.getInstance().getCrateHandler().updateCrate(player, crate);

        player.sendMessage(GREEN + "Updated crate items for kit `" + crate.getKitName() + "`");
    }

}

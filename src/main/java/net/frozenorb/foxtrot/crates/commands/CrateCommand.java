package net.frozenorb.foxtrot.crates.commands;

import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.crates.Crate;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class CrateCommand {

    @Command(value = {"hctcrate",})
    @Permission(value = "op")
    public static void onGive(@Sender Player sender, @Name("kit") String kit) {
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
    
    @Command(value = "hctcrate give")
    @Permission(value = "op")
    public static void onGive(CommandSender sender, @Name("kit") String kit, @Name("target") Player target) {
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

    @Command(value = "hctcrate create")
    @Permission(value = "op")
    public static void onCreate(Player player, @Name("kit") String kit) {
        Crate crate = new Crate(kit);

        Foxtrot.getInstance().getCrateHandler().getCrates().put(kit.toLowerCase(), crate);
        player.sendMessage(GREEN + "Created an empty crate for kit `" + crate.getKitName() + "`");
    }

    @Command("hctcrate edit")
    @Permission(value = "op")
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

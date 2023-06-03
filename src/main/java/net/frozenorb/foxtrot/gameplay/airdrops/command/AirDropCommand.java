package net.frozenorb.foxtrot.gameplay.airdrops.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.airdrops.AirDropHandler;
import net.frozenorb.foxtrot.gameplay.airdrops.listener.AirDropListener;
import net.frozenorb.foxtrot.gameplay.airdrops.reward.AirDropReward;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@CommandAlias("airdrop|airdrops")
@CommandPermission("op")
public class AirDropCommand extends BaseCommand {

    @Subcommand("give")
    public void give(CommandSender player, @Flags("other") @Name("player") Player target, @Flags("other") @Default Integer amount1){
        int amount = (amount1 == null ? 1 : amount1);

        target.getInventory().addItem(ItemBuilder.copyOf(HCF.getInstance().getAirDropHandler().getItemStack()).amount(amount).build());
        player.sendMessage(CC.translate("&aGiven &f" + target.getName()  + " &a" + amount + " airdrops."));
        target.sendMessage(CC.translate("&aYou were given &f" + amount + " &7airdrops."));
    }

    @Subcommand("giveall")
    public void give(CommandSender player, @Flags("other") @Default Integer amount1){
        int amount = (amount1 == null ? 1 : amount1);

        for (Player target : Bukkit.getOnlinePlayers()){
            target.getInventory().addItem(ItemBuilder.copyOf(HCF.getInstance().getAirDropHandler().getItemStack()).amount(amount).build());
        }

        player.sendMessage(CC.translate("&aGiven everyone &f" + amount + " airdrops."));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&3&lAirDrop All"));
        Bukkit.broadcastMessage(CC.translate("&fEveryone has been given " + amount + " airdrops!"));
        Bukkit.broadcastMessage(CC.translate("&7Purchase AirDrops at &ffrozenhcf.tebex.io"));
        Bukkit.broadcastMessage("");
    }

    @Subcommand("view")
    public void view(Player player){
        new Menu(player, "Airdrops", 36){
            @Override
            public void tick() {
                int i = 0;
                for (AirDropReward airDropReward : HCF.getInstance().getAirDropHandler().getCache()) {
                    this.buttons[i++] = new Button(ItemBuilder.copyOf(airDropReward.getItemStack().clone())
                            .setLore(List.of(new String[]{"", "&3| &fChance: &e" + airDropReward.getChance()})).build())
                            .setClickAction(event -> {
                                event.setCancelled(true);

                                HCF.getInstance().getAirDropHandler().getCache().remove(airDropReward);
                                updateMenu();
                            });
                }
            }
        }.updateMenu();
    }

    @Subcommand("add")
    public void add(Player player, @Name("chance") double chance, @Optional String command) {
        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must have an item in your hand!");
            return;
        }

        player.sendMessage(CC.translate("&aAdded " + ItemUtils.getName(player.getItemInHand()) + " &ato the Airdrop Rewards."));
        HCF.getInstance().getAirDropHandler().getCache().add(new AirDropReward(player.getItemInHand(), chance, (command == null ? "none" : command), true));
    }

    @Subcommand("reload")
    public void reload(CommandSender player){
        final AirDropHandler airDropHandler = HCF.getInstance().getAirDropHandler();

        airDropHandler.saveLootTable();

        airDropHandler.getCache().clear();
        airDropHandler.loadLootTable();

        player.sendMessage(ChatColor.GREEN + "Reloaded Airdrops.");
    }

    public static boolean isAirdrop(ItemStack itemStack) {
        final ItemStack compareTo = HCF.getInstance().getAirDropHandler().getItemStack();

        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getType() == Material.AIR) {
            return false;
        }

        if (itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }

        if (itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty()) {
            return false;
        }

        return itemStack.getType() == compareTo.getType() && itemStack.getItemMeta().getDisplayName().startsWith(compareTo.getItemMeta().getDisplayName()) && itemStack.getItemMeta().getLore().get(0).equals(compareTo.getItemMeta().getLore().get(0));
    }
}

package net.frozenorb.foxtrot.commands.op;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("SetBal")
@CommandPermission("foxtrot.setbal")
public class SetBalCommand extends BaseCommand {


    @Default
    public static void setBal(CommandSender sender, @Name("target") @Flags("other") Player player, @Name("amount") Float amount) {
        if (amount > 10000 && sender instanceof Player && !sender.isOp()) {
            sender.sendMessage("§cYou cannot set a balance this high. This action has been logged.");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage("§cWhy are you trying to do that?");
            return;
        }


        if (amount > 250000 && sender instanceof Player) {
            sender.sendMessage("§cWhat the fuck are you trying to do?");
            return;
        }

        Player targetPlayer = HCF.getInstance().getServer().getPlayer(player.getUniqueId());
        EconomyHandler.setBalance(player.getUniqueId(), amount);

        if (sender != targetPlayer) {
            sender.sendMessage("§6Balance for §e" + player + "§6 set to §e$" + amount);
        }

        if (sender instanceof Player && (targetPlayer != null)) {
            String targetDisplayName = ((Player) sender).getDisplayName();
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + amount + "§a by §6" + targetDisplayName);
        } else if (targetPlayer != null) {
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + amount + "§a by §4CONSOLE§a.");
        }

        HCF.getInstance().getWrappedBalanceMap().setBalance(player.getUniqueId(), amount);
    }

}
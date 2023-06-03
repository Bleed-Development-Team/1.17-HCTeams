package net.frozenorb.foxtrot.commands.op;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("addbal|addbalance")
@CommandPermission("foxtrot.setbal")
public class AddBalanceCommand extends BaseCommand {


    @Default
    public void onSetBalCommand(CommandSender sender, @Flags("other") @Name("target") Player target, @Name("amount") Float amount) {
        if (amount > 10000 && sender instanceof Player && !sender.isOp()) {
            sender.sendMessage("§cYou cannot set a balance this high. This action has been logged.");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage("§cWhy are you trying to do that?");
            return;
        }

        if (amount > 250000 && sender != null) {
            sender.sendMessage("§cWhat the fuck are you trying to do?");
            return;
        }

        Player targetPlayer = HCF.getInstance().getServer().getPlayer(target.getUniqueId());
        EconomyHandler.setBalance(target.getUniqueId(), EconomyHandler.getBalance(target.getUniqueId()) + amount);

        if (sender != targetPlayer) {
            sender.sendMessage("§6Balance for §e" + target + "§6 set to §e$" + amount);
        }

        if (sender instanceof Player && (targetPlayer != null)) {
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + amount + "§a by §6" + sender.getName());
        } else if (targetPlayer != null) {
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + amount + "§a by §4CONSOLE§a.");
        }

        HCF.getInstance().getWrappedBalanceMap().setBalance(target.getUniqueId(), amount);
    }

}
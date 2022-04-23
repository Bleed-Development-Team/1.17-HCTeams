package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CommandAlias("addbal|addbalance")
@CommandPermission("foxtrot.setbal")
public class AddBalanceCommand extends BaseCommand {


    @Default
    public void onSetBalCommand(Player sender, @Flags("other") Player target, Float amount) {
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

        Player targetPlayer = Foxtrot.getInstance().getServer().getPlayer(target.getUniqueId());
        FrozenEconomyHandler.setBalance(target.getUniqueId(), FrozenEconomyHandler.getBalance(target.getUniqueId()) + amount);

        if (sender != targetPlayer) {
            sender.sendMessage("§6Balance for §e" + target + "§6 set to §e$" + amount);
        }

        if (sender instanceof Player && (targetPlayer != null)) {
            String targetDisplayName = sender.getDisplayName();
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + amount + "§a by §6" + targetDisplayName);
        } else if (targetPlayer != null) {
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + amount + "§a by §4CONSOLE§a.");
        }

        Foxtrot.getInstance().getWrappedBalanceMap().setBalance(target.getUniqueId(), amount);
    }

}
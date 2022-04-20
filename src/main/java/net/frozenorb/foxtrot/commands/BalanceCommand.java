package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

@CommandAlias("bal|econ|$|balance")
public class BalanceCommand extends BaseCommand {

    @Default
    public void balance(Player sender, @Optional OfflinePlayer player) {
        if (player == null) {
            player = Bukkit.getOfflinePlayer(sender.getUniqueId());
        }
        if (!player.hasPlayedBefore()){
            sender.sendMessage(CC.translate("&cThat player hasn't played before."));
            return;
        }

        if (sender.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(FrozenEconomyHandler.getBalance(sender.getUniqueId())));
        } else {
            sender.sendMessage(ChatColor.GOLD + "Balance of " + player.getName() + ": " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(FrozenEconomyHandler.getBalance(player.getUniqueId())));
        }
    }

}
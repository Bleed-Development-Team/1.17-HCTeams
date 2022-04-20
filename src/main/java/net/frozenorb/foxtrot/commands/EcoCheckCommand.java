package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@CommandAlias("ecocheck")
@CommandPermission("op")
public class EcoCheckCommand extends BaseCommand {


    @Default
    public static void ecoCheck(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            if (isBad(team.getBalance())) {
                sender.sendMessage(ChatColor.YELLOW + "Team: " + ChatColor.WHITE + team.getName());
            }
        }

        try {
            Map<UUID, Double> balances = FrozenEconomyHandler.getBalances();

            for (Map.Entry<UUID, Double> balanceEntry  : balances.entrySet()) {
                if (isBad(balanceEntry.getValue())) {
                    sender.sendMessage(ChatColor.YELLOW + "Player: " + ChatColor.WHITE + UUIDUtils.name(balanceEntry.getKey()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isBad(double bal) {
        return (Double.isNaN(bal) || Double.isInfinite(bal) || bal > 1_000_000D);
    }

}
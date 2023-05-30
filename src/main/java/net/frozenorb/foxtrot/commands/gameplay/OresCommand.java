package net.frozenorb.foxtrot.commands.gameplay;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Name;
import net.frozenorb.foxtrot.HCF;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("ores")
public class OresCommand extends BaseCommand {

    @Default
    public static void ores(Player sender, @Flags("other") @Name("target") Player player) {
        sender.sendMessage(ChatColor.AQUA + "Diamond mined: " + ChatColor.WHITE + HCF.getInstance().getDiamondMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.GREEN + "Emerald mined: " + ChatColor.WHITE + HCF.getInstance().getEmeraldMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.RED + "Redstone mined: " + ChatColor.WHITE + HCF.getInstance().getRedstoneMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.GOLD + "Gold mined: " + ChatColor.WHITE + HCF.getInstance().getGoldMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.GRAY + "Iron mined: " + ChatColor.WHITE + HCF.getInstance().getIronMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.BLUE + "Lapis mined: " + ChatColor.WHITE + HCF.getInstance().getLapisMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.DARK_GRAY + "Coal mined: " + ChatColor.WHITE + HCF.getInstance().getCoalMinedMap().getMined(player.getUniqueId()));
    }

}
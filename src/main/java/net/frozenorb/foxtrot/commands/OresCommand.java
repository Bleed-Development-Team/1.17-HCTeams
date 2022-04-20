package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("ores")
public class OresCommand extends BaseCommand {

    @Default
    public static void ores(Player sender, @Flags("other") Player player) {
        sender.sendMessage(ChatColor.AQUA + "Diamond mined: " + ChatColor.WHITE + Foxtrot.getInstance().getDiamondMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.GREEN + "Emerald mined: " + ChatColor.WHITE + Foxtrot.getInstance().getEmeraldMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.RED + "Redstone mined: " + ChatColor.WHITE + Foxtrot.getInstance().getRedstoneMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.GOLD + "Gold mined: " + ChatColor.WHITE + Foxtrot.getInstance().getGoldMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.GRAY + "Iron mined: " + ChatColor.WHITE + Foxtrot.getInstance().getIronMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.BLUE + "Lapis mined: " + ChatColor.WHITE + Foxtrot.getInstance().getLapisMinedMap().getMined(player.getUniqueId()));
        sender.sendMessage(ChatColor.DARK_GRAY + "Coal mined: " + ChatColor.WHITE + Foxtrot.getInstance().getCoalMinedMap().getMined(player.getUniqueId()));
    }

}
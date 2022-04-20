package net.frozenorb.foxtrot.map.stats.command;

import me.vaperion.blade.annotation.*;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.economy.FrozenEconomyHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public class StatsCommand {

    @Command(value = {"stats"})
    public static void stats(@Sender CommandSender sender, @Optional(value = "self") OfflinePlayer player) {
        UUID uuid = player.getUniqueId();

        if (!player.hasPlayedBefore()){
            sender.sendMessage(CC.translate("&cThat player hasn't played before."));
            return;
        }


        int kills = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(uuid).getKills();
        int deaths = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(uuid).getDeaths();

        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 36));
        sender.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.WHITE + "'s  Statistics" + ChatColor.GRAY + ":");
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 36));

        sender.sendMessage(CC.translate(" &f» &bKills&7: &f" + kills));
        sender.sendMessage(CC.translate(" &f» &bDeaths&7: &f" + deaths));
        sender.sendMessage(CC.translate(" &f» &bBalance&7: &f$" + NumberFormat.getNumberInstance(Locale.US).format(FrozenEconomyHandler.getBalance(uuid))));
        sender.sendMessage(CC.translate(" &f» &bKDR&7: &f" + (deaths == 0 ? "Infinity" : Team.DTR_FORMAT.format((double) kills / (double) deaths))));
        sender.sendMessage(CC.translate(" &f» &bRaidable Teams&7 &f" + Foxtrot.getInstance().getRaidableTeamsMap().getRaidableTeams(uuid)));
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 36));
    }

    @Command(value = {"clearallstats"})
    @Permission(value = "op")
    public static void clearallstats(@Sender Player sender) {
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to clear all stats? Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    Foxtrot.getInstance().getMapHandler().getStatsHandler().clearAll();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "All stats cleared!");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

}

package net.frozenorb.foxtrot.commands.gameplay.help;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.HCF;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

@CommandAlias("?|help")
public class HelpCommand extends BaseCommand {

    @Default
    public static void help( Player sender) {
        String sharp = "Sharpness " + Enchantment.DAMAGE_ALL.getMaxLevel();
        String prot = "Protection " + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel();
        String bow = "Power " + Enchantment.ARROW_DAMAGE.getMaxLevel();

        String serverName = HCF.getInstance().getServerHandler().getServerName();
        String serverWebsite = HCF.getInstance().getServerHandler().getNetworkWebsite();

        /*sender.sendMessages("§6§m-----------------------------------------------------",
                "§9Helpful Commands:",
                "§e/report <player> <reason> §7- Report cheaters!",
                "§e/request <message> §7- Request staff assistance.",
                "§e/tgc §7- Toggle chat visibility.",

                "",
                "§9Other Information:",
                "§eOfficial Teamspeak §7- §dts." + serverWebsite,
                "§e" + serverName + " Rules §7- §dwww." + serverWebsite + "/rules",
                "§eStore §7- §dwww." + serverWebsite + "/store",
                "§6§m-----------------------------------------------------");

         */
    }

}

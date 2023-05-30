package net.frozenorb.foxtrot.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import com.google.common.io.Files;
import net.frozenorb.foxtrot.HCF;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

@CommandAlias("Revive")
@CommandPermission("foxtrot.revive")
public class ReviveCommand extends BaseCommand {


    @Default
    public static void revive(Player sender, @Name("target") OfflinePlayer player, @Name("reason") String reason) {
        if (reason.equals(".")) {
            sender.sendMessage(ChatColor.RED + ". is not a good reason...");
            return;
        }

        if (HCF.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            File logTo = new File(new File("foxlogs"), "adminrevives.log");

            try {
                logTo.createNewFile();
                Files.append("[" + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "] " + sender.getName() + " revived " + player.getName() + " for " + reason + "\n", logTo, Charset.defaultCharset());
            } catch (Exception e) {
                e.printStackTrace();
            }

            HCF.getInstance().getDeathbanMap().revive(player.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Revived " + player.getName() + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
        }
    }

}

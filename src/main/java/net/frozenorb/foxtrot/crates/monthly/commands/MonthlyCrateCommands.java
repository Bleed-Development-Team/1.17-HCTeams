package net.frozenorb.foxtrot.crates.monthly.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.util.crates.CrateUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("mcrate")
@CommandPermission("hcf.admin")
public class MonthlyCrateCommands extends BaseCommand {

    @Subcommand("give")
    public static void execute(CommandSender sender, @Flags("other") @Name("target") Player target, @Flags("other") @Name("crateName") String crateName, @Flags("other") @Name("amount") int amount) {
        if (!HCF.getInstance().getConfig().getConfigurationSection("crates").getKeys(false).contains(crateName)) {
            sender.sendMessage(CrateUtil.c("&c&l(!) &cThat is an invalid crate name!"));
            return;
        }

        CrateUtil.giveCrate(crateName, target, amount, sender);
    }

    @Subcommand("giveall")
    public static void giveall(CommandSender sender, @Flags("other") @Name("crateName") String crateName, @Flags("other") @Name("amount") int amount) {
        if (!HCF.getInstance().getConfig().getConfigurationSection("crates").getKeys(false).contains(crateName)) {
            sender.sendMessage(CrateUtil.c("&c&l(!) &cThat is an invalid crate name!"));
            return;
        }

        for (Player onlinePlayer : HCF.getInstance().getServer().getOnlinePlayers()) {
            CrateUtil.giveCrate(crateName, onlinePlayer, amount, sender);
        }
    }
}

package net.frozenorb.foxtrot.commands.op;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("dev")
@CommandPermission("hcf.dev")
public class DevCommand extends BaseCommand {

    @Subcommand("setupKOTH")
    public void setupKOTH(Player player, @Flags("other") @Name("koth") String name){
        player.chat("/f create " + name);
        player.chat("/f opclaim");

        player.sendMessage(CC.translate("&aDone"));
    }

    @Subcommand("setupKOTH2")
    public void setupKOTH2(Player player, @Flags("other") @Name("koth") String name){
        player.chat("/flags remove " + name + " Safe-zone");
        player.chat("/flags add " + name + " KOTH");

        player.sendMessage(CC.translate("&aDone"));
    }

    @Subcommand("setupCitadel")
    public void setupCitadel(Player player){
        player.chat("/flags remove Citadel Safe-zone");
        player.chat("/flags add Citadel Citadel");
        player.chat("/flags add Citadel No-Enderpearl");

        player.sendMessage(CC.translate("&aDone"));
    }
}

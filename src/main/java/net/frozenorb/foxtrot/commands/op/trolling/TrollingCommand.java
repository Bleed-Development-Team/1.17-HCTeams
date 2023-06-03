package net.frozenorb.foxtrot.commands.op.trolling;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.commands.op.trolling.reach.ReachListener;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@CommandAlias("trolling")
@CommandPermission("op")
@Private
public class TrollingCommand extends BaseCommand {

    private boolean enabled = false;

    @Subcommand("enable")
    public void enable(Player player){
        if (!player.getName().equals("Wordpass")){
            player.sendMessage(CC.translate("yo ur not allowed to do only embry"));
            return;
        }

        enabled = !enabled;
        player.sendMessage(CC.translate("it gort toggeld " + enabled));
    }

    @Subcommand("nocooldowns")
    public void noCooldowns(Player player){
        if (!enabled){
            player.sendMessage(CC.translate("&cit isnt enabled by embry sorry mate"));
            return;
        }

        if (player.hasMetadata("nocds")){
            player.removeMetadata("nocds", HCF.getInstance());
        } else {
            player.setMetadata("nocds", new FixedMetadataValue(HCF.getInstance(), "nocds"));
        }

        player.sendMessage(CC.translate("&fYou have toggled the trolling thing " + (player.hasMetadata("nocds") ? "&aon" : "&coff") + "&f."));
    }

    @Subcommand("reach")
    public void reach(Player player, @Name("reach") double amount){
        if (!enabled){
            player.sendMessage(CC.translate("&cit isnt enabled by embry sorry mate"));
            return;
        }

        if (ReachListener.reach.containsKey(player.getUniqueId())){
            ReachListener.reach.remove(player.getUniqueId());
            player.sendMessage(CC.translate("&fYour reach was toggled &coff"));
            return;
        } else {
            ReachListener.reach.put(player.getUniqueId(), amount);
        }

        player.sendMessage(CC.translate("&aYour reach has been set to &f" + amount + "&a."));
    }

    @Subcommand("fakeop")
    public void fakeOp(Player player, @Name("player") Player target){
        //target.sendMessage();
    }

    @Subcommand("crash")
    public void crash(Player player, @Flags("other") @Name("player") Player target){
        if (!enabled){
            player.sendMessage(CC.translate("&cit isnt enabled by embry sorry mate"));
            return;
        }

        Bukkit.dispatchCommand(player, "execute as " + target.getName() + " at @s run particle flame ~ ~ ~ 1 1 1 1 999999999 force @s");
    }
}

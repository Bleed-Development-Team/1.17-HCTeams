package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.server.event.EnderpearlCooldownAppliedEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("pearl")
@CommandPermission("op")
public class EnderpearlCommands extends BaseCommand {


    @Subcommand("add")
    public static void enderpearlRemove(Player player, @Flags("other") Player target) {
        if (target == null) {
            target = player;
        }
        long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(target.getLocation()) ? 30_000L : Foxtrot.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1_000L : 16_000L;

        EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(target, timeToApply);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(appliedEvent);

        EnderpearlCooldownHandler.getEnderpearlCooldown().put(target.getName(), System.currentTimeMillis() + appliedEvent.getTimeToApply());
        player.sendMessage(CC.translate("&aAdded pearl timer."));
    }


    @Subcommand("remove")
    public static void enderpearlAdd(Player player, Player target) {
        if (target == null) {
            target = player;
        }
        if (EnderpearlCooldownHandler.getEnderpearlCooldown().get(target.getName()) == null) {
            player.sendMessage(CC.translate("&cNo pearl timer found."));
            return;
        }

        EnderpearlCooldownHandler.getEnderpearlCooldown().remove(target.getName());

        player.sendMessage(CC.translate("&aReset pearl timer."));
    }

}

package net.frozenorb.foxtrot.commands.op;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.server.event.EnderpearlCooldownAppliedEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("pearl")
@CommandPermission("op")
public class EnderpearlCommands extends BaseCommand {


    @Subcommand("add")
    public static void enderpearlRemove(Player player, @Flags("other") @Name("target") Player target) {
        if (target == null) {
            target = player;
        }
        long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(target.getLocation()) ? 30_000L : HCF.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1_000L : 16_000L;

        EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(target, timeToApply);
        HCF.getInstance().getServer().getPluginManager().callEvent(appliedEvent);

        EnderpearlCooldownHandler.getEnderpearlCooldown().put(target.getName(), System.currentTimeMillis() + appliedEvent.getTimeToApply());
        player.sendMessage(CC.translate("&aAdded pearl timer."));
    }


    @Subcommand("remove")
    public static void enderpearlAdd(Player player, @Name("target") Player target) {
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

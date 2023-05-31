package net.frozenorb.foxtrot.commands.gameplay;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Name;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LettingInCommand extends BaseCommand {

    public LettingInCommand(){
        Cooldown.createCooldown("lettingIn");
    }

    @CommandAlias("lettingin")
    public void lettingIn(Player player, @Name("baseheight") int baseHeight){
        final Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (Cooldown.isOnCooldown("lettingIn", player)){
            player.sendMessage(CC.translate("&cYou must wait another &l" + TimeUtils.formatIntoDetailedString(Cooldown.getCooldownForPlayerInt("lettingIn", player)) + " &cto use this command again!"));
            return;
        }

        if (team == null){
            player.sendMessage(CC.translate("&7You are not in a team!"));
            return;
        }

        if (team.getHQ() == null){
            player.sendMessage(CC.translate("&cYou do not have an HQ set!"));
            return;
        }

        if (team.isRaidable()){
            player.sendMessage(CC.translate("&cYou cannot do this while your team is raidable!"));
            return;
        }

        if (baseHeight <= 1 || baseHeight > 256){
            player.sendMessage(CC.translate("&cYour base height must be at least 2 high and the maximum is 256 blocks high."));
            return;
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&b&l" + team.getName() +  " &fis letting in!"));
        Bukkit.broadcastMessage(CC.translate("&b| &fHQ: &3" + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockZ()));
        Bukkit.broadcastMessage(CC.translate("&b| &fBase Height: &3" + baseHeight));
        Bukkit.broadcastMessage(CC.translate("&b| &fOnline Members: &3" + team.getOnlineMembers().size()));
        Bukkit.broadcastMessage(CC.translate("&b| &fDTR: " + team.getFormattedDTR()));
        if (DTRHandler.isOnCooldown(team)){
            player.sendMessage(CC.translate("&b| &fRegen: &3" + TimeUtils.formatIntoDetailedString(((int) (team.getDTRCooldown() - System.currentTimeMillis()) / 1000))));
        }
        Bukkit.broadcastMessage("");

        Cooldown.addCooldown("lettingIn", player, 60);
    }
}

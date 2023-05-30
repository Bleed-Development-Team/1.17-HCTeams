package net.frozenorb.foxtrot.server.listener.impl;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TitleListener implements Listener {

    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()){
            player.sendTitle(CC.translate("&b&lFrozenHCF"), CC.translate("&fWelcome to FrozenHCF!"));

            Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                player.sendTitle(CC.translate("&b&lGet Started"), CC.translate("&fCreate a faction by typing /f create [name]"));

                player.sendMessage(CC.translate(""));
                player.sendMessage(CC.translate("&b&lWelcome"));
                player.sendMessage(CC.translate("&fWelcome to FrozenHCF."));
                player.sendMessage(CC.translate(""));
                player.sendMessage(CC.translate("&b&lGet Started"));
                player.sendMessage(CC.translate("&fType &b/f create [name] &fto create a faction."));
                player.sendMessage(CC.translate(""));

            }, 20*8);

            return;
        }

        final Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            if (team == null) {
                player.sendTitle(CC.translate("&b&lGet Started"), CC.translate("&fCreate a faction by typing /f create [name]"));
                return;
            }

            if (team.getClaims().isEmpty()){
                player.sendTitle(CC.translate("&b&lGet Started"), CC.translate("&fFind an open &7Wilderness &farea and type &b/f claim&f!"));
            }
        }, 20*8);
    }

}

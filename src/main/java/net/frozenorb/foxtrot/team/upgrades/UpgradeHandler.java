package net.frozenorb.foxtrot.team.upgrades;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;

public class UpgradeHandler implements Runnable {

    public UpgradeHandler(){
        //Bukkit.getScheduler().runTaskTimer(HCF.getInstance(), this, 0, 35);
    }

    public void giveEffects(Team team, Player player){
        player.setMetadata("activeEffects", new FixedMetadataValue(HCF.getInstance(), true));

        team.getFactionUpgrades().forEach(
            type -> type.getEffects().forEach(
                player::addPotionEffect
            )
        );
    }

    public void removeEffects(Team team, Player player){
        player.removeMetadata("activeEffects", HCF.getInstance());

        team.getFactionUpgrades().forEach(
            type -> type.getEffects().forEach(
                potionEffect -> player.removePotionEffect(potionEffect.getType())
            )
        );
    }

    public void removeEffects(Player player){
        player.removeMetadata("activeEffects", HCF.getInstance());

        Arrays.stream(Upgrade.values()).forEach(
            value -> value.getEffects().forEach(
                effect -> player.removePotionEffect(effect.getType())
            )
        );
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()){
            Team teamAt = LandBoard.getInstance().getTeam(player.getLocation());
            Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());

            if (teamAt == null) continue;

            if (player.hasMetadata("activeEffects") && !teamAt.isMember(player.getUniqueId())){
                if (team != null){
                    removeEffects(team, player);
                } else {
                    removeEffects(player);
                }
            } else if (teamAt.isMember(player.getUniqueId()) && !player.hasMetadata("activeEffects")) {
                giveEffects(teamAt, player);
            }
        }
    }
}

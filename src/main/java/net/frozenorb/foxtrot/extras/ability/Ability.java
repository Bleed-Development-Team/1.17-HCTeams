package net.frozenorb.foxtrot.extras.ability;

import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.entity.Player;

public abstract class Ability {

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getAuthor();
    public abstract String getCooldownID();
    public abstract long getCooldown();

    public int getCooldownSeconds(){
        return (int) (getCooldown() / 1000L);
    }

    public String getCooldownFormatted(Player player){
        return Cooldown.getCooldownString(player, getCooldownID());
    }

    public boolean isOnCooldown(Player player){
        return Cooldown.isOnCooldown(getCooldownID(), player);
    }

    public boolean isOnGlobalCooldown(Player player){
        return Cooldown.isOnCooldown("partner", player);
    }

    public void applySelf(Player player){
        Cooldown.addCooldown(getCooldownID(), player, getCooldownSeconds());

        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&c❤ &6You have used the &f" + getName() + " &6ability&6!"));
        player.sendMessage(CC.translate("&c❤ &6" + getDescription()));
        player.sendMessage(CC.translate("&c❤ &6You are now on cooldown for &f" + getCooldownFormatted(player) + "&6."));
        player.sendMessage(CC.translate(""));
    }

    public void applyOther(Player player, Player victim){
        Cooldown.addCooldown(getCooldownID(), player, getCooldownSeconds());

        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&c❤ &6You have hit &f" + victim + " &6with the &f" + getName() + " &6ability&6!"));
        player.sendMessage(CC.translate("&c❤ &6" + getDescription()));
        player.sendMessage(CC.translate("&c❤ &6You are now on cooldown for &f" + getCooldownFormatted(player) + "&6."));
        player.sendMessage(CC.translate(""));

        victim.sendMessage(CC.translate("&c❤ &6You have been hit by &f" + player + " &6with the &f" + getName() + " &6ability&6!"));
    }
}

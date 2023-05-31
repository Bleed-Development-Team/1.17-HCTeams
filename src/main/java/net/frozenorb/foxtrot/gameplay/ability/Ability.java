package net.frozenorb.foxtrot.gameplay.ability;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.commands.op.TrollingCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class Ability implements Listener {

    public Ability(){
        Bukkit.getPluginManager().registerEvents(this, HCF.getInstance());
    }

    public abstract String getID();
    public abstract String getName();
    public abstract int getCooldown();
    public abstract ItemStack getItemStack();
    public abstract String getColor();

    public String getUncoloredName(){
        return ChatColor.stripColor(getName());
    }

    public boolean canUse(Player player){
        if (HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())){
            player.sendMessage(CC.translate("&cYou are currently on &aPvP Timer&c."));
            return false;
        }

        if (DTRBitmask.CITADEL.appliesAt(player.getLocation())){
            player.sendMessage(CC.translate("&cYou cannot use abilities in &5&lCitadel&c."));
            return false;
        } else if (DTRBitmask.KOTH.appliesAt(player.getLocation())){
            player.sendMessage(CC.translate("&cYou cannot use abilities in &6&lKOTH&c."));
            return false;
        } else if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())){
            player.sendMessage(CC.translate("&cYou cannot use abilities in a &a&lSafe-Zone&c."));
            return false;
        }

        return true;
    }

    public void use(Player player){
        addCooldown(player);
        useItem(player);
    }

    public void use(Player player, String t){
        addCooldown(player);
    }

    public void addCooldown(Player player){
        if (player.hasMetadata("nocds")) return;
        Cooldown.addCooldown(getID(), player, getCooldown());
    }

    public void useItem(Player player){
        if (player.getItemInHand().getAmount() == 1){
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }
    }

    public void sendMessage(Player player, String message){
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate(getName()));
        player.sendMessage(CC.translate(getColor() + "| &fYou have used the " + getName() + " &fability."));
        player.sendMessage(CC.translate(getColor() + "| &f" + message));
        player.sendMessage(CC.translate(getColor() + "| &fYou are now on cooldown for " + getColor() + TimeUtils.formatIntoDetailedString(getCooldown()) + "&f."));
        player.sendMessage(CC.translate(""));
    }


    public boolean victimCheck(Player damager, Player victim){
        if (damager == victim) return false;

        if (DTRBitmask.SAFE_ZONE.appliesAt(victim.getLocation())){
            damager.sendMessage(CC.translate("&cThat player is in a &aSafe-Zone&c."));
            return false;
        } else if (DTRBitmask.CITADEL.appliesAt(victim.getLocation())){
            damager.sendMessage(CC.translate("&cThat player is in a &5Citadel&c."));
            return false;
        } else if (DTRBitmask.KOTH.appliesAt(victim.getLocation())){
            damager.sendMessage(CC.translate("&cThat player is in a &bKOTH&c."));
            return false;
        }
        if (HCF.getInstance().getPvPTimerMap().hasTimer(victim.getUniqueId())) {
            damager.sendMessage(CC.translate("&cThat player is currently on &aPvP Timer&c."));
            return false;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(damager.getUniqueId());

        if (team != null){
            return !team.isMember(victim.getUniqueId());
        }

        return true;
    }
}

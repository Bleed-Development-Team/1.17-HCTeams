package net.frozenorb.foxtrot.extras.ability;

import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Ability implements Listener {

    public abstract String getName();
    public abstract String getUncoloredName();
    public abstract String getDescription();
    public abstract String getCooldownID();
    public abstract int getCooldown();
    public abstract Material getMaterial();


    public boolean isSimilarTo(ItemStack item, ItemStack compareTo){

        boolean hasItemMeta = item.hasItemMeta();
        boolean compareToHasItemMeta = compareTo.hasItemMeta();

        boolean itemHasDisplayName = false;
        boolean compareToHasDisplayName = false;

        String itemDisplayName = "";

        String compareToDisplayName = "";

        List<String> itemLore = new ArrayList<>();
        List<String> compareToLore = new ArrayList<>();

        if(hasItemMeta && compareToHasItemMeta){
            itemHasDisplayName = item.getItemMeta().hasDisplayName();
            compareToHasDisplayName = item.getItemMeta().hasDisplayName();
            if(itemHasDisplayName) itemDisplayName = item.getItemMeta().getDisplayName();
            if(compareToHasDisplayName) compareToDisplayName = compareTo.getItemMeta().getDisplayName();
            if(item.getItemMeta().hasLore()) itemLore = item.getItemMeta().getLore();
            if(compareTo.getItemMeta().hasLore()) compareToLore = compareTo.getItemMeta().getLore();
        }

        return item.getType() == compareTo.getType() &&
                hasItemMeta &&
                itemHasDisplayName &&
                compareToHasDisplayName &&
                itemDisplayName.equals(compareToDisplayName) && itemLore.containsAll(compareToLore);
    }

    public String getCooldownFormatted(Player player){
        return Cooldown.getCooldownString(player, getCooldownID());
    }

    public void giveCooldowns(Player player){
        Cooldown.addCooldown(getCooldownID(), player, getCooldown());
        Cooldown.addCooldown("partner", player, 10);
    }

    public boolean canUse(Player player){
        if (isOnGlobalCooldown(player)){
            player.sendMessage(CC.translate("&cYou are still on cooldown for &d&lPartner &cfor another &c&l" + Cooldown.getCooldownString(player,"partner") + "&c."));
            return false;
        }

        if (isOnCooldown(player)){
            player.sendMessage(CC.translate("&cYou are on cooldown for the " + getName() + " &cfor another &c&l" + getCooldownFormatted(player) + "&c."));
            return false;
        }

        if (DTRBitmask.CITADEL.appliesAt(player.getLocation())){
            player.sendMessage(CC.translate("&cYou cannot use abilities in &5&lCitadel&c."));
            return false;
        } else         if (DTRBitmask.CITADEL.appliesAt(player.getLocation())){
            player.sendMessage(CC.translate("&cYou cannot use abilities in &5&lCitadel&c."));
            return false;
        }

    }

    public boolean isOnCooldown(Player player){
        return Cooldown.isOnCooldown(getCooldownID(), player);
    }

    public boolean isOnGlobalCooldown(Player player){
        return Cooldown.isOnCooldown("partner", player);
    }

    public void applyDir(Player player, Player victim){
        Cooldown.addCooldown(getCooldownID(), player, getCooldown());
        Cooldown.addCooldown("partner", player, 10);

        player.sendMessage(CC.translate("&c❤ &6You have switched positions with &f" + victim.getName() + "&6!"));
        victim.sendMessage(CC.translate("&c❤ &f" + player.getName() + " &6has switched positions with you!"));
    }

    public void applySelf(Player player){
        Cooldown.addCooldown(getCooldownID(), player, getCooldown());
        Cooldown.addCooldown("partner", player, 10);

        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&c❤ &6You have used the &f" + getUncoloredName() + " &6ability&6!"));
        player.sendMessage(CC.translate("&c❤ &6" + getDescription()));
        player.sendMessage(CC.translate("&c❤ &6You are now on cooldown for &f" + getCooldownFormatted(player) + "&6."));
        player.sendMessage(CC.translate(""));

        if (player.getItemInHand().getAmount() > 1) {
            int amount = player.getItemInHand().getAmount() - 1;
            player.getItemInHand().setAmount(amount);
        } else {
            player.setItemInHand(null);
        }
    }

    public void applyOther(Player player, Player victim){
        Cooldown.addCooldown(getCooldownID(), player, getCooldown());
        Cooldown.addCooldown("partner", player, 10);

        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&c❤ &6You have hit &f" + victim.getName() + " &6with the &f" + getUncoloredName() + " &6ability&6!"));
        player.sendMessage(CC.translate("&c❤ &6" + getDescription()));
        player.sendMessage(CC.translate("&c❤ &6You are now on cooldown for &f" + getCooldownFormatted(player) + "&6."));
        player.sendMessage(CC.translate(""));

        victim.sendMessage(CC.translate("&c❤ &6You have been hit by &f" + player.getName() + " &6with the &f" + getName() + " &6ability&6!"));

        if (player.getItemInHand().getAmount() > 1) {
            int amount = player.getItemInHand().getAmount() - 1;
            player.getItemInHand().setAmount(amount);
        } else {
            player.setItemInHand(null);
        }
    }
}

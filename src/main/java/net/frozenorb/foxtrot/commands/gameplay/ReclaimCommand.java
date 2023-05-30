package net.frozenorb.foxtrot.commands.gameplay;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.persist.maps.ReclaimMap;
import net.frozenorb.foxtrot.util.CC;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ReclaimCommand extends BaseCommand {

    private LuckPerms api = LuckPermsProvider.get();

    @CommandAlias("reclaim")
    public void reclaim(Player player){
        ReclaimMap map = HCF.getInstance().getReclaimMap();

        if (map.hasReclaimed(player.getUniqueId())){
            player.sendMessage(CC.translate("&cYou have already reclaimed!"));
            return;
        }

        String group = cap(getPlayerGroup(player));

        if (!HCF.getInstance().getConfig().contains("reclaim." + group)){
            player.sendMessage(CC.translate("&cThere is no reclaim setup for your rank."));
            return;
        }

        //in this case, we have found their rank and now give their reclaim.
        String rank = getDisplayName(player);

        List<String> commands = HCF.getInstance().getConfig().getStringList("reclaim." + getPlayerGroup(player));

        for (String command : commands){
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", player.getName()));
        }

        Bukkit.broadcastMessage(CC.translate("&3" + player.getName() + " &fhas reclaimed their &b" + getPlayerGroup(player) + " &rrank."));
        player.sendTitle(CC.translate("&a&lPerks Claimed"), CC.translate("&7You have reclaimed your perks."));

        // put them in the reclaim map.
        map.setReclaimed(player.getUniqueId(), true);
    }

    @CommandAlias("resetreclaim")
    public void reclaimReset(Player player) {
        ReclaimMap map = HCF.getInstance().getReclaimMap();

        map.setReclaimed(player.getUniqueId(), false);

        player.sendMessage(CC.translate("&aYou have reset your reclaim."));
    }

    private String getPlayerGroup(Player player){
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user == null) return "";

        return user.getPrimaryGroup();
    }

    private String getDisplayName(Player player){
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user == null) return "";

        String displayName = api.getGroupManager().getGroup(user.getPrimaryGroup()).getDisplayName();

        displayName = displayName.replaceAll("\\[", "").replaceAll("\\]","").toLowerCase();
        displayName = displayName.substring(0, 1).toUpperCase() + getPlayerGroup(player).substring(1);

        return displayName;
    }

    private String cap(String input){
        String firstChar = input.substring(0, 1).toUpperCase();
        String restOfString = input.substring(1).toLowerCase();

        return firstChar + restOfString;
    }
}

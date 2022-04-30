package net.frozenorb.foxtrot.server.rank;

import ltd.matrixstudios.alchemist.api.AlchemistAPI;
import ltd.matrixstudios.alchemist.models.profile.GameProfile;
import ltd.matrixstudios.alchemist.service.ranks.RankService;
import me.clip.placeholderapi.PlaceholderAPI;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TopRankTask implements Runnable {

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        List<String> ranks = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            GameProfile profile = AlchemistAPI.INSTANCE.quickFindProfile(player.getUniqueId());

            if (profile.getCurrentRank().getId().equals("fox")) {
                ranks.add(player.getName());
                builder.append("&r").append(player.getName()).append("&7, ");
            }
        }

        if (ranks.isEmpty()) return;

        Bukkit.broadcastMessage(CC.translate("&7&m----------------------------------"));
        Bukkit.broadcastMessage(CC.translate("&6&lOnline Donator Ranks"));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&r" + builder.substring(0, builder.toString().length() - 2)));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&7&m----------------------------------"));
    }
}

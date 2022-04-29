package net.frozenorb.foxtrot.server.rank;

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

        for (User user : getUsersInGroup("test").join()) {
            ranks.add(user.getUsername());
            builder.append(CC.translate("&f" + user.getUsername())).append(CC.translate("&7, "));
        }

        if (ranks.isEmpty()) return;

        Bukkit.broadcastMessage("&7&m----------------------------------");
        Bukkit.broadcastMessage("&4&lOnline Donator Ranks");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("&r" + builder.substring(0, builder.toString().length() - 2));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("&7&m----------------------------------");
    }

    private CompletableFuture<List<User>> getUsersInGroup(String groupName) {
        NodeMatcher<InheritanceNode> matcher = NodeMatcher.key(InheritanceNode.builder(groupName).build());
        LuckPerms luckperms = LuckPermsProvider.get();

        return luckperms.getUserManager().searchAll(matcher).thenComposeAsync(results -> {
            List<CompletableFuture<User>> users = new ArrayList<>();

            return CompletableFuture.allOf(
                    results.keySet().stream()
                            .map(uuid -> luckperms.getUserManager().loadUser(uuid))
                            .peek(users::add)
                            .toArray(CompletableFuture[]::new)
            ).thenApply(x -> users.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList())
            );
        });
    }
}

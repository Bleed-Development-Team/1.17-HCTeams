package net.frozenorb.foxtrot.server.listener.impl;

import com.google.common.collect.ImmutableSet;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.util.RegenUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BlockRegenListener implements Listener {

    private static final Set<Material> REGEN = ImmutableSet.of(
            Material.COBBLESTONE,
            Material.DIRT,
            Material.OAK_LOG,
            Material.NETHERRACK,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES
    );

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (event.isCancelled() || HCF.getInstance().getServerHandler().isAdminOverride(player)) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        if ((team == null || !team.isMember(event.getPlayer().getUniqueId())) && (player.getItemInHand() != null && REGEN.contains(player.getItemInHand().getType()))) {
            RegenUtils.schedule(event.getBlock(), 1, TimeUnit.HOURS, (block) -> {}, (block) -> {
                Team currentTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

                return !(currentTeam != null && currentTeam.isMember(player.getUniqueId()));
            });
        }
    }

}

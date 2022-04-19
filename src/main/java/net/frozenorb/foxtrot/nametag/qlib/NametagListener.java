/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.foxtrot.nametag.qlib;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public final class NametagListener implements Listener {
    NametagListener() {
    }

    /*
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (FrozenNametagHandler.isInitiated()) {
            event.getPlayer().setMetadata("qLibNametag-LoggedIn", new FixedMetadataValue(Foxtrot.getInstance(), true));

            FrozenNametagHandler.initiatePlayer(event.getPlayer());
            FrozenNametagHandler.reloadPlayer(event.getPlayer());
            FrozenNametagHandler.reloadOthersFor(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("qLibNametag-LoggedIn", Foxtrot.getInstance());
        FrozenNametagHandler.getTeamMap().remove(event.getPlayer().getName());
    }

     */
}


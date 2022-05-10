package net.frozenorb.foxtrot.extras.replay.listener;

import net.frozenorb.foxtrot.extras.replay.ReplayManager;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ReplayListener implements Listener {

    @EventHandler
    public void onEntityLaunch(ProjectileLaunchEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof EnderPearl)) return;

        //ReplayManager.getInstance().startReplay(event);
    }
}

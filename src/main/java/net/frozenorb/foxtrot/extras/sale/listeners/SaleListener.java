package net.frozenorb.foxtrot.extras.sale.listeners;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.sale.Sale;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class SaleListener implements Listener {

    @EventHandler
    public void onCommand(ServerCommandEvent event) {
        if (event.getSender() instanceof Player) return;

        String command = event.getCommand();

        for (Sale sale : Foxtrot.getInstance()) {
    }
}

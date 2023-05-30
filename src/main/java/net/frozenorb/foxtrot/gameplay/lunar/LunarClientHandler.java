package net.frozenorb.foxtrot.gameplay.lunar;

import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.lunar.listener.ClientListener;
import org.bukkit.Bukkit;

public class LunarClientHandler {

    public LCWaypoint currentEventWaypoint = null;

    public LunarClientHandler(){
        ClientListener clientListener = new ClientListener();
        clientListener.currentEventWaypoint = currentEventWaypoint;

        Bukkit.getServer().getPluginManager().registerEvents(clientListener, HCF.getInstance());
    }
}

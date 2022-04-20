package net.frozenorb.foxtrot.extras.lunar;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.lunar.listener.ClientListener;
import org.bukkit.Bukkit;

public class LunarClientHandler {


    public LunarClientHandler(){
        Bukkit.getServer().getPluginManager().registerEvents(new ClientListener(), Foxtrot.getInstance());
    }
}

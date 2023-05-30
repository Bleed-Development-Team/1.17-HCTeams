package net.frozenorb.foxtrot.gameplay.events.koth.listeners;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.koth.events.EventControlTickEvent;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KOTHListener implements Listener {

    @EventHandler
    public void onKOTHControlTick(EventControlTickEvent event) {
        
        if (event.getKOTH().getType() != EventType.KOTH) {
            return;
        }

        KOTH koth = (KOTH) event.getKOTH();
        if (koth.getRemainingCapTime() % 180 == 0 && koth.getRemainingCapTime() <= (koth.getCapTime() - 30)) {
            HCF.getInstance().getServer().broadcastMessage(CC.translate("&3&l[KOTH] " + ChatColor.BLUE + koth.getName() + ChatColor.WHITE + " is trying to be controlled."));
            HCF.getInstance().getServer().broadcastMessage(CC.translate(ChatColor.WHITE + " - Time left: " + ChatColor.BLUE + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime())));
        }
    }

}
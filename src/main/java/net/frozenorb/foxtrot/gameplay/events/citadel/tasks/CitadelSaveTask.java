package net.frozenorb.foxtrot.gameplay.events.citadel.tasks;

import net.frozenorb.foxtrot.HCF;
import org.bukkit.scheduler.BukkitRunnable;

public class CitadelSaveTask extends BukkitRunnable {

    public void run() {
        HCF.getInstance().getCitadelHandler().saveCitadelInfo();
    }

}
package net.frozenorb.foxtrot.chat.tasks;

import net.frozenorb.foxtrot.HCF;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveCustomPrefixesTask extends BukkitRunnable {

    public void run() {
        HCF.getInstance().getChatHandler().saveCustomPrefixes();
    }

}
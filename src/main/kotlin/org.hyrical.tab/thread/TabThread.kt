package net.frozenorb.foxtrot.tab.thread

import net.frozenorb.foxtrot.HCF
import org.bukkit.Bukkit

class TabThread : Thread() {

    override fun run() {
        while (true){
            try {
                for (player in Bukkit.getOnlinePlayers()){
                    if (!HCF.getInstance().tabManager.skins.containsKey(player.uniqueId.toString())) continue
                    val tab = HCF.getInstance().tabManager.tablists[player.uniqueId]

                    tab?.update()
                }
            } catch (ex: Exception){
                ex.printStackTrace()
            }
            try {
                sleep(200L)
            } catch (ex: InterruptedException){
                ex.printStackTrace()
            }
        }
    }

}
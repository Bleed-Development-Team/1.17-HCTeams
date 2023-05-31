package net.frozenorb.foxtrot.tab.thread

import net.frozenorb.foxtrot.HCF
import org.bukkit.Bukkit

class TabThread : Thread() {

    override fun run() {
        while (true){
            try {
                for (player in Bukkit.getOnlinePlayers()){
                    Bukkit.broadcastMessage("entering loop for testing ${player.name} ${HCF.getInstance().tabManager.skins.containsKey(player.uniqueId.toString())}")

                    val tab = HCF.getInstance().tabManager.tablists[player.uniqueId]

                    if (tab == null) {
                        Bukkit.broadcastMessage("Tab for ${player.name} was null")
                    }

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
package net.frozenorb.foxtrot.tab.listener

import net.frozenorb.foxtrot.HCF
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import net.frozenorb.foxtrot.tab.Tab

class TabListener : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent){
        HCF.getInstance().tabManager.tablists.remove(event.player.uniqueId)
    }

    @EventHandler
    fun onQuit(event: PlayerJoinEvent){
        val player = event.player

        object : BukkitRunnable(){
            override fun run() {
                val tab = Tab(player)
                HCF.getInstance().tabManager.tablists[player.uniqueId] = tab
            }
        }.runTaskLaterAsynchronously(HCF.getInstance(), 10L)
    }
}
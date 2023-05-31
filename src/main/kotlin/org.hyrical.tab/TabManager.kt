package net.frozenorb.foxtrot.tab

import net.frozenorb.foxtrot.HCF
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import net.frozenorb.foxtrot.tab.extra.TabSkin
import net.frozenorb.foxtrot.tab.listener.TabListener
import net.frozenorb.foxtrot.tab.packet.TabPacket
import net.frozenorb.foxtrot.tab.thread.TabThread
import java.util.*

class TabManager(val adapter: TabAdapter) {
    val tablists: HashMap<UUID, Tab> = hashMapOf()
    val skins: HashMap<String, TabSkin> = hashMapOf()

    init {
        this.load()
        // start tablist thread
        TabThread().start()
        Bukkit.getPluginManager().registerEvents(TabListener(), HCF.getInstance())
    }

    private fun load(){
        for (s in HCF.getInstance().tabFile.getConfigurationSection("SKINS")!!.getKeys(false)){
            val path = "SKINS.$s"
            this.skins[s] = TabSkin(HCF.getInstance().tabFile.getString("$path.SIGNATURE")!!, HCF.getInstance().tabFile.getString("$path.VALUE")!!)
        }


    }

    fun createPacket(player: Player): TabPacket {
        return TabVersioning.versionToPacket("1_16_R3").getConstructor(Player::class.java).newInstance(player)
    }
    /*
        val skin = "net.frozenorb.foxtrot.tab.packet.type.TablistPacketV" + VersionManager.getNMSVer()
        return Class.forName(skin).getConstructor(Player::class.java)
            .newInstance(player) as TabPacket
    }

     */

}
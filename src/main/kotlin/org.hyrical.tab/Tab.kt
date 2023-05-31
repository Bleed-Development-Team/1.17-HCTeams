package net.frozenorb.foxtrot.tab

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import net.frozenorb.foxtrot.HCF
import org.bukkit.entity.Player
import net.frozenorb.foxtrot.tab.extra.TabEntry
import net.frozenorb.foxtrot.tab.packet.TabPacket
import org.bukkit.Bukkit

class Tab(val player: Player) {
    val entries: Table<Int, Int, TabEntry> = HashBasedTable.create()
    val tabPacket: TabPacket = HCF.getInstance().tabManager.createPacket(player)
    val EMPTY_ENTRY: TabEntry = TabEntry("", -1)

    fun update() {
        entries.clear()
        tabPacket.update()
    }


    fun getEntries(x: Int, y: Int): TabEntry {
        val entry: TabEntry? = entries.get(x, y)

        if (entry == null) {
            entries.put(x, y, this.EMPTY_ENTRY)

            return this.EMPTY_ENTRY
        }
        return entry
    }


    fun add(x: Int, y: Int, name: String) {
        entries.put(x, y, TabEntry(name, -1))
    }

    fun getPacket(): TabPacket {
        return tabPacket
    }

    fun add(x: Int, y: Int, name: String, ping: Int) {
        entries.put(x, y, TabEntry(name, ping))
    }
}
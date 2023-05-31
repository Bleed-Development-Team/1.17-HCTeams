package net.frozenorb.foxtrot.tab

import net.frozenorb.foxtrot.tab.packet.TabPacket
import net.frozenorb.foxtrot.tab.packet.type.TablistPacketV1_16_R3

object TabVersioning {

    private val packets = mapOf(
        "1_16_R3" to TablistPacketV1_16_R3::class.java
    )

    fun versionToPacket(version: String): Class<TabPacket> {
        return packets[version]!! as Class<TabPacket>
    }
}
package net.frozenorb.foxtrot.tab

import org.hyrical.hcf.provider.nametag.packet.NametagPacket
import org.hyrical.hcf.provider.nametag.packet.type.NametagPacketV1_16_R3
import org.hyrical.hcf.provider.nametag.packet.type.NametagPacketV1_7_R4
import org.hyrical.hcf.provider.nametag.packet.type.NametagPacketV1_8_R3
import net.frozenorb.foxtrot.tab.packet.TabPacket
import net.frozenorb.foxtrot.tab.packet.type.TablistPacketV1_16_R3
import net.frozenorb.foxtrot.tab.packet.type.TablistPacketV1_7_R4
import net.frozenorb.foxtrot.tab.packet.type.TablistPacketV1_8_R3

object TabVersioning {

    private val packets = mapOf(
        "1_7_R4" to TablistPacketV1_7_R4::class.java,
        "1_8_R3" to TablistPacketV1_8_R3::class.java,
        "1_16_R3" to TablistPacketV1_16_R3::class.java
    )

    fun versionToPacket(version: String): Class<TabPacket> {
        return packets[version]!! as Class<TabPacket>
    }
}
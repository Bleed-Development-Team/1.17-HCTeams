package net.frozenorb.foxtrot.tab.packet.type

import com.google.common.collect.HashBasedTable
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.frozenorb.foxtrot.HCF
import net.frozenorb.foxtrot.tab.Tab
import net.frozenorb.foxtrot.tab.extra.TabEntry
import net.frozenorb.foxtrot.tab.packet.TabPacket
import net.minecraft.server.v1_16_R3.*
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*


class TablistPacketV1_16_R3(val player2: Player) : TabPacket(player2) {
    private var LOADED = false
    private var footer: String = ""
    private var header: String = ""
    private val maxColumns = if (100 >= 47) 4 else 3
    private val FAKE_PLAYERS: HashBasedTable<Int, Int, EntityPlayer>? = HashBasedTable.create()

    fun loadFakes() {
        if (!this.LOADED) {
            this.LOADED = true
            val minecraftServer = MinecraftServer.getServer()
            val worldServer: WorldServer = minecraftServer.worlds.iterator().next()
            for (i in 0..19) {
                for (f in 0..3) {
                    val part = if (f === 0) "LEFT" else if (f === 1) "MIDDLE" else if (f === 2) "RIGHT" else "FAR_RIGHT"
                    val line: String = HCF.getInstance().tabFile.getStringList(part).get(i).split(";").get(0)
                    val profile = GameProfile(UUID.randomUUID(), getName(f, i))
                    val player = EntityPlayer(minecraftServer, worldServer, profile, PlayerInteractManager(worldServer))
                    val skin= HCF.getInstance().tabManager.skins[line]
                    profile.properties.put("textures", Property("textures", skin?.value, skin?.signature))
                    FAKE_PLAYERS?.put(f, i, player)
                }
            }
        }
    }

    private fun sendPacket(packet: Packet<*>) {
        val playerConnection = (player as CraftPlayer).handle.playerConnection
        playerConnection?.sendPacket(packet)
    }

    fun init(){
        for (i in 0..19) {
            for (f in 0..maxColumns) {
                val player: EntityPlayer = this.FAKE_PLAYERS!!.get(f, i)
                sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, player))
            }
        }
    }

    init {
        try {
            this.loadFakes()
            this.init()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun sendHeaderFooter() {
        val header = HCF.getInstance().tabManager.adapter.getHeader(player)
        val footer = HCF.getInstance().tabManager.adapter.getFooter(player)

        header.replace("[", "")
        header.replace("]", "")
        footer.replace("[", "")
        footer.replace("]", "")
        val packet = PacketPlayOutPlayerListHeaderFooter()

        packet.header = IChatBaseComponent.ChatSerializer.a("{\"text\":\"$header\"}")
        packet.footer = IChatBaseComponent.ChatSerializer.a("{\"text\":\"$footer\"}")

        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }


    override fun update() {
        sendHeaderFooter()
        val tablist: Tab = HCF.getInstance().tabManager.adapter.getInfo(player)
        for (i in 0..19) {
            for (f in 0 until maxColumns) {
                val entry: TabEntry = tablist.getEntries(f, i)
                val player: EntityPlayer = this.FAKE_PLAYERS!!.get(f, i)
                if (player.ping != entry.ping) {
                    player.ping = entry.ping
                    sendPacket(
                        PacketPlayOutPlayerInfo(
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY,
                            player
                        )
                    )
                }
                handleTeams(player.bukkitEntity, entry.text, calcSlot(f, i))
            }
        }
    }

}
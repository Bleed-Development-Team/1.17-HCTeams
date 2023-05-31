package net.frozenorb.foxtrot.tab.packet

import net.frozenorb.foxtrot.HCF
import net.frozenorb.foxtrot.provider.nametags.Nametag
import net.frozenorb.foxtrot.provider.nametags.extra.NameVisibility
import org.apache.commons.lang3.StringUtils
import org.bukkit.ChatColor
import org.bukkit.entity.Player


abstract class TabPacket(open val player: Player) {
    abstract fun update()

    fun getName(x: Int, y: Int): String {
        val array = y.toString().toCharArray()
        return "§" + x + if (y >= 10) "§" + array[0] + "§" + array[1] else "§" + array[0]
    }

    open fun calcSlot(x: Int, y: Int): Int {
        return y + if (x == 0) 0 else if (x == 1) 20 else if (x == 2) 40 else 60
    }

    fun handleTeams(player: Player?, text: String, pos: Int) {
        val tag: Nametag = HCF.getInstance().nametagManager.nametags[this.player.uniqueId] ?: return
        val name = "00000000000000" + if (pos >= 10) Integer.valueOf(pos) else "0$pos"
        if (text.length > 16) {
            var text1 = text.substring(0, 16)
            var text2 = text.substring(16)
            if (text1.endsWith("§")) {
                text1 = text1.substring(0, text1.toCharArray().size - 1)
                text2 = StringUtils.left(ChatColor.getLastColors(text1) + "§" + text2, 16)
            } else {
                text2 = StringUtils.left(ChatColor.getLastColors(text1) + text2, 16)
            }
            tag.packet.create(name, "", text1, text2, false, NameVisibility.ALWAYS)
        } else {
            tag.packet.create(name, "", text, "", false, NameVisibility.ALWAYS)
        }

        tag.packet.addToTeam(player, name)
    }
}
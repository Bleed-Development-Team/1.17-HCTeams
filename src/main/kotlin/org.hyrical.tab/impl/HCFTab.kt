package net.frozenorb.foxtrot.tab.impl

import com.google.common.collect.Lists
import net.frozenorb.foxtrot.HCF
import net.frozenorb.foxtrot.economy.EconomyHandler
import net.frozenorb.foxtrot.tab.Tab
import net.frozenorb.foxtrot.tab.TabAdapter
import net.frozenorb.foxtrot.team.Team
import net.frozenorb.foxtrot.team.claims.LandBoard
import net.frozenorb.foxtrot.util.CC
import net.frozenorb.foxtrot.util.DirectionUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import java.text.NumberFormat
import java.util.*
import java.util.stream.Collectors
import kotlin.math.round


class HCFTab : TabAdapter {
    private var farRightTablist: MutableList<String> = mutableListOf()
    private var leftTablist: MutableList<String> = mutableListOf()
    private var middleTablist: MutableList<String> = mutableListOf()
    private var rightTablist: MutableList<String> = mutableListOf()

    init {
        leftTablist = HCF.getInstance().tabFile.getStringList("LEFT")
        middleTablist = HCF.getInstance().tabFile.getStringList("MIDDLE")
        rightTablist = HCF.getInstance().tabFile.getStringList("RIGHT")
        farRightTablist = HCF.getInstance().tabFile.getStringList("FAR_RIGHT")
    }

    override fun getHeader(player: Player): String {
        return CC.translate(HCF.getInstance().tabFile.getString("HEADER")!!)
    }

    override fun getInfo(player: Player): Tab {
        val tablist: Tab = HCF.getInstance().tabManager.tablists[player.uniqueId]!!
        val team = HCF.getInstance().teamHandler.getTeam(player.uniqueId)

        for (i in 0..19) {
            tablist.add(0, i, CC.translate(leftTablist[i]))
            tablist.add(1, i, CC.translate(middleTablist[i]))
            tablist.add(2, i, CC.translate(rightTablist[i]))
            tablist.add(3, i, CC.translate(farRightTablist[i]))
        }

        val teams = HCF.getInstance().teamHandler.teams.stream().filter { it.onlineMembers.isNotEmpty() && it.owner != null}
            .sorted { t1, t2 -> t2.onlineMembers.size - t1.onlineMembers.size }
            .collect(Collectors.toList())


        for (entry in tablist.entries.values()) {
            var text: String = entry.text
            if (text.isEmpty()) {
                continue
            }
            if (team != null) {
                val teamFormat: List<String> = HCF.getInstance().tabFile.getStringList("TEAM-INFO.HAS-TEAM")
                var owner: UUID? = null
                val coleaders: MutableList<UUID> = Lists.newArrayList()
                val captains: MutableList<UUID> = Lists.newArrayList()
                val members: MutableList<UUID> = Lists.newArrayList()
                for (member in team.onlineMembers) {
                    if (team.isOwner(member.uniqueId)) {
                        owner = member.uniqueId
                    } else if (team.isCoLeader(member.uniqueId)) {
                        coleaders.add(member.uniqueId)
                    } else if (team.isCaptain(member.uniqueId)) {
                        captains.add(member.uniqueId)
                    } else {
                        members.add(member.uniqueId)
                    }
                }

                val teamIndices: MutableList<UUID> = mutableListOf()

                owner?.let {
                    teamIndices.add(0, it)
                }

                teamIndices.addAll(coleaders)

                teamIndices.addAll(captains)

                teamIndices.addAll(members)


                text = text.replace("%team-name%", team.getName(player))

                for (i in teamFormat.indices) {
                    val replacedText: String = teamFormat[i].replace("%dtr-color%", team.dtrColor.toString())
                        .replace("%dtr%", team.formattedDTR)
                        .replace("%dtr-symbol%", team.dtrSuffix)
                        .replace("%players%", team.onlineMembers.size.toString())
                        .replace("%max%", team.members.size.toString())
                        .replace("%balance%", NumberFormat.getInstance().format(team.balance))
                        .replace("%points%", team.points.toString())
                        .replace("%home%", team.formattedHQ)
                        .replace("%team-name%", team.name)

                    text = text.replace("%teaminfo-$i%", replacedText)
                }

                for (i in teamIndices.indices) {
                    val teamPlayer = Bukkit.getPlayer(teamIndices[i]) ?: continue

                    text = text.replace("%member-$i%", "&7${
                        if (owner == teamPlayer.uniqueId) {
                            "***"
                        } else if (coleaders.contains(teamPlayer.uniqueId)) {
                            "**"
                        } else if (captains.contains(teamPlayer.uniqueId)) {
                            "*"
                        } else {
                            ""
                        }
                    }&a${teamPlayer.name}")
                }
            } else {
                val noTeamFormat: List<String> = HCF.getInstance().tabFile.getStringList("TEAM-INFO.NOT-SET")

                text = text.replace("%team-name%", "")
                for (i in noTeamFormat.indices) {
                    val tt = noTeamFormat[i]
                    text = text.replace("%teaminfo-$i%", tt)
                }
            }
            var i = 0


            while (i < teams.size && i != 19) {
                val targetTeam: Team = teams[i]

                val listFormat = HCF.getInstance().tabFile.getString("TEAM-LIST-FORMAT")!!
                    .replace("%relation-color%", getRelationColor(targetTeam, player))//argetTeam.getRelationColor(player) TODO: Embry
                    .replace("%name%", targetTeam.name)
                    .replace("%online%", targetTeam.onlineMembers.size.toString())
                    .replace("%max%", targetTeam.members.size.toString())
                    .replace("%dtr-color%", targetTeam.dtrColor.toString())
                    .replace("%dtr%", targetTeam.formattedDTR)

                text = text.replace("%team-$i%", listFormat)
                ++i
            }
            if (text.contains("%team-") || text.contains("%teaminfo-") || text.contains("%member-")) {
                entry.text = ""
            } else {
                val stats = HCF.getInstance().mapHandler.statsHandler.getStats(player.uniqueId)
                val balance = EconomyHandler.getBalance(player.uniqueId)

                val location: String;

                val loc: Location = player.location
                val ownerTeam = LandBoard.getInstance().getTeam(loc)

                if (ownerTeam != null) {
                    location = ownerTeam.getName(player.player)
                } else if (!HCF.getInstance().getServerHandler().isWarzone(loc)) {
                    location = "&7The Wilderness"
                } else if (LandBoard.getInstance().getTeam(loc) != null && LandBoard.getInstance()
                        .getTeam(loc).name.equals("citadel", ignoreCase = true)
                ) {
                    location = "&5&lCitadel"
                } else {
                    location = "&4Warzone"
                }


                entry.text = text.replace("%kills%", stats.kills.toString())
                    .replace("%deaths%", stats.deaths.toString())
                    .replace("%balance%", NumberFormat.getInstance().format(balance))
                    .replace("%current-killstreak%", stats.killstreak.toString())
                    .replace("%highest-killstreak%", stats.highestKillstreak.toString())
                    .replace("%claim%", location)
                    .replace("%x%", (round(player.location.x).toInt()).toString())
                    .replace("%z%", (round(player.location.z).toInt()).toString())
                    .replace("%facing%", DirectionUtils.getCardinalDirection(player)!!)
                    .replace("%online%", Bukkit.getOnlinePlayers().size.toString())
                    .replace("%max%", Bukkit.getMaxPlayers().toString())
            }
            entry.text = CC.translate(entry.text)
        }

        return tablist
    }

    override fun getFooter(player: Player): String {
        return CC.translate(HCF.getInstance().tabFile.getString("FOOTER")!!).replace("%players%", Bukkit.getOnlinePlayers().size.toString())

    }

    fun load(){
        for (i in 0..19) {
            val left: List<String> = this.leftTablist[i].split(";")
            this.leftTablist[i] = if (left.size == 1) "" else left[1]
            val middle: List<String> = this.middleTablist[i].split(";")
            this.middleTablist[i] = if (middle.size == 1) "" else middle[1]
            val right: List<String> = this.rightTablist[i].split(";")
            this.rightTablist[i] = if (right.size == 1) "" else right[1]
            val farRight: List<String> = this.farRightTablist[i].split(";")
            this.farRightTablist[i] = if (farRight.size == 1) "" else farRight[1]
        }
    }

    private fun getRelationColor(team: Team, player: Player): String {
        return if (team.isMember(player.uniqueId)) "&a" else "&c"
    }
}
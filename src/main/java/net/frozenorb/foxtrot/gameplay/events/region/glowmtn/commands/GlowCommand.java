package net.frozenorb.foxtrot.gameplay.events.region.glowmtn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowMountain;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

@CommandAlias("glow")
@CommandPermission("op")
public class GlowCommand extends BaseCommand {


    @Subcommand("scan")
    public static void glowScan(Player sender) {
        Team team = HCF.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());

        // Make sure we have a team
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must first create the team (" + GlowHandler.getGlowTeamName() + ") and claim it!");
            return;
        }

        // Make sure said team has a claim
        if (team.getClaims().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You must claim land for '" + GlowHandler.getGlowTeamName() + "' before scanning it!");
            return;
        }

        // We have a claim, and a team, now do we have a glowstone?
        if (!HCF.getInstance().getGlowHandler().hasGlowMountain()) {
            HCF.getInstance().getGlowHandler().setGlowMountain(new GlowMountain());
        }

        // We have a glowstone now, we're gonna scan and save the area
        HCF.getInstance().getGlowHandler().getGlowMountain().scan();
        HCF.getInstance().getGlowHandler().save(); // save to file :D

        sender.sendMessage(GREEN + "[Glowstone Mountain] Scanned all glowstone and saved glowstone mountain to file!");
    }


    @Subcommand("reset")
    public static void glowReset(Player sender) {
        Team team = HCF.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());

        // Make sure we have a team, claims, and a mountain!
        if (team == null || team.getClaims().isEmpty() || !HCF.getInstance().getGlowHandler().hasGlowMountain()) {
            sender.sendMessage(RED + "Create the team '" + GlowHandler.getGlowTeamName() + "', then make a claim for it, finally scan it! (/glow scan)");
            return;
        }

        // Check, check, check, LIFT OFF! (reset the mountain)
        HCF.getInstance().getGlowHandler().getGlowMountain().reset();

        Bukkit.broadcastMessage(GOLD + "[Glowstone Mountain]" + GREEN + " All glowstone has been reset!");
    }
}
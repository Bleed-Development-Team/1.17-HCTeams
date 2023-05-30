package net.frozenorb.foxtrot.gameplay.events.citadel.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.base.Joiner;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

@CommandAlias("citadel")
public class CitadelCommand extends BaseCommand {

    // Make this pretty.
    @Default
    public static void citadel(Player sender) {
        Set<ObjectId> cappers = HCF.getInstance().getCitadelHandler().getCappers();
        Set<String> capperNames = new HashSet<>();

        for (ObjectId capper : cappers) {
            Team capperTeam = HCF.getInstance().getTeamHandler().getTeam(capper);

            if (capperTeam != null) {
                capperNames.add(capperTeam.getName());
            }
        }

        if (!capperNames.isEmpty()) {
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was captured by " + ChatColor.GREEN + Joiner.on(", ").join(capperNames) + ChatColor.YELLOW + ".");
        } else {
            Event citadel = HCF.getInstance().getEventHandler().getEvent("Citadel");

            if (citadel != null && citadel.isActive()) {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel can be captured now.");
            } else {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was not captured last week.");
            }
        }

        Date lootable = HCF.getInstance().getCitadelHandler().getLootable();
        sender.sendMessage(ChatColor.GOLD + "Citadel: " + ChatColor.WHITE + "Lootable " + (lootable.before(new Date()) ? "now" : "at " + (new SimpleDateFormat()).format(lootable) + (capperNames.isEmpty() ? "." : ", and lootable now by " + Joiner.on(", ").join(capperNames) + ".")));
    }


    @HelpCommand
    @CommandPermission("op")
    public static void citadelHelp(Player player){
        List<String> lines = new ArrayList<>();

        lines.add("&c/citadel loadloottable - Loads the citadel loot in your inventory.");
        lines.add("&c/citadel rescanchests - [Developer use only]");
        lines.add("&c/citadel respawnchests - Respawn all chests inside of citadel");
        lines.add("&c/citadel save - Saves all information about Citadel in a config file.");
        lines.add("&c/citadel saveloottable - Saves the loot.");
        lines.add("&c/citadel setcapper - Sets the citadel cappers.");

        CC.send(lines, player);
    }
    @Subcommand("loadloottable")
    @CommandPermission("op")
    public static void citadelLoadLoottable(Player sender) {
        sender.getInventory().clear();
        int itemIndex = 0;

        for (ItemStack itemStack : HCF.getInstance().getCitadelHandler().getCitadelLoot()) {
            sender.getInventory().setItem(itemIndex, itemStack);
            itemIndex++;
        }

        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Loaded Citadel loot into your inventory.");
    }


    @Subcommand("rescanchests")
    @CommandPermission("op")
    public static void citadelRescanChests(Player sender) {
        HCF.getInstance().getCitadelHandler().scanLoot();
        HCF.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Rescanned all Citadel chests.");
    }

    @Subcommand("respawnchests")
    @CommandPermission("op")
    public static void citadelRespawnChests(Player sender) {
        HCF.getInstance().getCitadelHandler().respawnCitadelChests();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Respawned all Citadel chests.");
    }
    @Subcommand("save")
    @CommandPermission("op")
    public static void citadelSave(Player sender) {
        HCF.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Saved Citadel info to file.");
    }

    @Subcommand("setcapper")
    @CommandPermission("op")
    public static void citadelSetCapper(Player sender, String cappers) {
        if (cappers.equals("null")) {
            HCF.getInstance().getCitadelHandler().resetCappers();
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Reset Citadel cappers.");
        } else {
            String[] teamNames = cappers.split(",");
            List<ObjectId> teams = new ArrayList<>();

            for (String teamName : teamNames) {
                Team team = HCF.getInstance().getTeamHandler().getTeam(teamName);

                if (team != null) {
                    teams.add(team.getUniqueId());
                } else {
                    sender.sendMessage(ChatColor.RED + "Team '" + teamName + "' cannot be found.");
                    return;
                }
            }

            HCF.getInstance().getCitadelHandler().getCappers().clear();
            HCF.getInstance().getCitadelHandler().getCappers().addAll(teams);
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Updated Citadel cappers.");
        }
    }

    @Subcommand("saveloottable")
    @CommandPermission("op")
    public static void citadelSaveLoottable(Player sender) {
        HCF.getInstance().getCitadelHandler().getCitadelLoot().clear();

        for (ItemStack itemStack : sender.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                HCF.getInstance().getCitadelHandler().getCitadelLoot().add(itemStack);
            }
        }

        HCF.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Saved Citadel loot from your inventory.");
    }

}
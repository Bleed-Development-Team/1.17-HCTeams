package net.frozenorb.foxtrot.gameplay.events.koth.summoner.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KOTHSummonerCommand extends BaseCommand {

    @CommandAlias("kothsummoner")
    @CommandPermission("koth.kothsummoner")
    public void kothSummoner(Player player){
        player.getInventory().addItem(ItemBuilder.of(Material.LEVER)
                .name("&b&lKOTH Summoner").addToLore("", "&b&l| &fRight click this to", "&b&l| &fsummon a KOTH automatically.", "", "&7This can be found in KOTH Crates.").build());
    }
}

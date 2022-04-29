package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.DeathbanUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CommandAlias("applydeathbankit")
public class ApplyDeathbanKit extends BaseCommand {

    public static List<UUID> people = new ArrayList<>();

    @Default
    public void applydeathban(Player player) {
        if (!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) return;

        player.getInventory().clear();
        DeathbanUtils.giveKit(player);

        people.add(player.getUniqueId());
    }

    @Subcommand("revive")
    public void applyRevive(Player player){
        if (!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) return;

        if (Foxtrot.getInstance().getFriendLivesMap().getLives(player.getUniqueId()) <= 0){
            player.sendMessage(CC.translate("&cYou don't have any lives to revive yourself."));
            return;
        }

        people.remove(player.getUniqueId());
        DeathbanUtils.putOutOfDeathban(player, "revoveovioeqwivajqwfkqwl");
    }

}

package net.frozenorb.foxtrot.extras.ability.impl.portable;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.Ability;
import net.frozenorb.foxtrot.extras.ability.util.Items;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class RegenerationAbility extends Ability {
    @Override
    public String getName() {
        return "&d&lRegeneration III";
    }

    @Override
    public String getUncoloredName() {
        return "Regeneration III";
    }

    @Override
    public String getDescription() {
        return "Right click to receive 3 seconds of Regeneration III.";
    }

    @Override
    public String getCooldownID() {
        return "portable";
    }

    @Override
    public int getCooldown() {
        return 60 * 2;
    }

    @Override
    public ItemStack getItemStack() {
        return Items.getRegen();
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (!isSimilarTo(player.getItemInHand(), Items.getRegen())) return;
            if (!canUse(player)) return;

            int i = 0;
            for (Player friendly : getNearbyPlayers(player, true)) {
                friendly.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 3, 2));
                i++;
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 3, 2));

            giveCooldowns(player);
            useItem(player);

            player.sendMessage(CC.translate("&c❤ &6You have gave " + (i == 0 ? "yourself" : "&f" + i + " &6player" + (i == 1 ? "" : "s")) + "  &fRegeneration III&6."));
        }
    }

    public List<Player> getNearbyPlayers(Player player, boolean friendly) {
        List<Player> valid = new ArrayList<>();
        Team sourceTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity entity : player.getNearbyEntities(20, 20 / 2, 20)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;

                if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(nearbyPlayer.getUniqueId())) {
                    continue;
                }

                if (sourceTeam == null) {
                    if (!friendly) {
                        valid.add(nearbyPlayer);
                    }

                    continue;
                }

                boolean isFriendly = sourceTeam.isMember(nearbyPlayer.getUniqueId());
                boolean isAlly = sourceTeam.isAlly(nearbyPlayer.getUniqueId());

                if (friendly && isFriendly) {
                    valid.add(nearbyPlayer);
                } else if (!friendly && !isFriendly && !isAlly) { // the isAlly is here so you can't give your allies negative effects, but so you also can't give them positive effects.
                    valid.add(nearbyPlayer);
                }
            }
        }

        valid.add(player);
        return (valid);
    }
}

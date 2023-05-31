package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.gameplay.ability.damage.type.Freezer;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.util.Cooldown;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TimeStone extends InteractAbility {
    @Override
    public String getID() {
        return "timestone";
    }

    @Override
    public String getName() {
        return "&a&lTime Stone";
    }

    @Override
    public int getCooldown() {
        return 60 * 4;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.EMERALD)
                .name(getName())
                .addToLore("&7Right click to remove all your", "&7partner item cooldowns.")
                .build();
    }

    @Override
    public String getColor() {
        return "&a";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        for (Ability ability : HCF.getInstance().getAbilityHandler().getAllAbilities()){
            if (ability instanceof Freezer) continue;

            if (Cooldown.isOnCooldown(ability.getID(), player)){
                Cooldown.removeCooldown(ability.getID(), player);
            }
        }

        use(player);
        sendMessage(player, "All your partner item cooldowns have been reset.");
    }
}

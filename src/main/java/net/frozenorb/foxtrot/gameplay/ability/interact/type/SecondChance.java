package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SecondChance extends InteractAbility {
    @Override
    public String getID() {
        return "secondchance";
    }

    @Override
    public String getName() {
        return "&6&lSecond Chance";
    }

    @Override
    public int getCooldown() {
        return 60 * 3;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.GHAST_TEAR)
                .name(getName())
                .addToLore("&7Right click to reset your enderpearl", "&7cooldown.")
                .build();
    }

    @Override
    public String getColor() {
        return "&6";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        if (EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) == null) {
            player.sendMessage(CC.translate("&cYou are not on enderpearl cooldown."));
            return;
        }

        EnderpearlCooldownHandler.getEnderpearlCooldown().remove(player.getName());
        sendMessage(player, "&fYour enderpearl cooldown has been set to " + getColor() + 0 + "&f.");

        use(player);
    }
}

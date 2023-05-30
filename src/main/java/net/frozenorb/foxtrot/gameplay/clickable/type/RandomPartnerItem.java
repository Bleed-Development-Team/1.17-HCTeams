package net.frozenorb.foxtrot.gameplay.clickable.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.clickable.ClickableItem;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RandomPartnerItem extends ClickableItem {
    @Override
    public String getID() {
        return "randompartneritem";
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.BOOK).name("&3&lRandom Partner Item")
                .addToLore("", "&3| &fRight click to receive a ", "&3| randomized partner item.")
                .build();
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public String getCooldownID() {
        return "";
    }

    @Override
    public void handle(PlayerInteractEvent event) {
        takeItem(event.getPlayer());

        Ability ability = HCF.getInstance().getAbilityHandler().getAllAbilities().get(
                HCF.RANDOM.nextInt(HCF.getInstance().getAbilityHandler().getAllAbilities().size())
        );

        event.getPlayer().getInventory().addItem(ability.getItemStack());
    }
}

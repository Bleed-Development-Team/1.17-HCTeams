package net.frozenorb.foxtrot.gameplay.clickable.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.clickable.ClickableItem;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GemsPack extends ClickableItem {

    @Override
    public String getID() {
        return "gems";
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.BOOK)
                .name("&3&lGems &fPack")
                .addToLore("", "&3| &fRight click to give yourself 10 gems!")
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
        Player player = event.getPlayer();

        HCF.getInstance().getGemsMap().addGems(player.getUniqueId(), 10);
        player.sendMessage(CC.translate("&aSuccesfully redeemed &21 &agem pack containing &2+10 &agems."));

        takeItem(player);
    }
}

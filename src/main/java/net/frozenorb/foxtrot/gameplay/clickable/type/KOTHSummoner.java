package net.frozenorb.foxtrot.gameplay.clickable.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.clickable.ClickableItem;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class KOTHSummoner extends ClickableItem {
    @Override
    public String getID() {
        return "summoner";
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.LEVER)
                .name("&4&lKOTH Summoner")
                .addToLore("", "&4| &fRight click this to summon", "&4| &fa KOTH automatically.", "", "&fFound in &4&lKOTH &fcrate.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
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

        for (Event otherKoth : HCF.getInstance().getEventHandler().getEvents()) {
            if (otherKoth.isActive()) {
                player.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active. You cannot do this now.");
                return;
            }
        }

        try {
            Event[] eventArray = HCF.getInstance().getEventHandler().getEvents().toArray(new Event[0]);
            Event koth = eventArray[HCF.RANDOM.nextInt(eventArray.length)];

            koth.activate();
        } catch (Exception ex){
            ex.printStackTrace();
            player.sendMessage(CC.translate("&cAn error has occurred, please report this to an administrator."));
            return;
        }

        takeItem(player);
        event.setCancelled(true);
    }
}

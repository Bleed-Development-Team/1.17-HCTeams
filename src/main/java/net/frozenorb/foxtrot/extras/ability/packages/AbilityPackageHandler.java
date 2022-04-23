package net.frozenorb.foxtrot.extras.ability.packages;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class AbilityPackageHandler implements Listener {
    @EventHandler
    public void onAbilityPackage(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!isSimilarTo(player.getItemInHand(), Foxtrot.getInstance().getAbilityPackage().getPackage()))  return;
        ItemStack partnerPackage = null;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item.equals(Foxtrot.getInstance().getAbilityPackage().getPackage())) {
                partnerPackage = item;
                break;
            }
        }
        if (partnerPackage == null) return;

        int inventorySize = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            inventorySize++;
        }
        //if (inventorySize >= 33) {
            //player.sendMessage("§cYou do not have enough inventory space to open the package.");
            //return;
        //}        partnerPackage.setAmount(partnerPackage.getAmount() - 1);
{
        if (player.getItemInHand().getAmount() > 1) {
            int amount = player.getItemInHand().getAmount() - 1;
            player.getItemInHand().setAmount(amount);
            } else {
                player.setItemInHand(null);
            }
        }
        int p = Foxtrot.RANDOM.nextInt(0, Foxtrot.getInstance().getAbilityHandler().getAbilities().size());
        int pp = Foxtrot.RANDOM.nextInt(0, Foxtrot.getInstance().getAbilityHandler().getAbilities().size());
        int ppp = Foxtrot.RANDOM.nextInt(0, Foxtrot.getInstance().getAbilityHandler().getAbilities().size());
        for (int i = 0; i <= 3; i++) {
            player.getInventory().addItem(Foxtrot.getInstance().getAbilityHandler().getAbilities().get(pp).getItemStack());
            player.getInventory().addItem(Foxtrot.getInstance().getAbilityHandler().getAbilities().get(ppp).getItemStack());
            player.getInventory().addItem(Foxtrot.getInstance().getAbilityHandler().getAbilities().get(p).getItemStack());
        }

        Firework firework = (Firework)player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta data = firework.getFireworkMeta();
        data.addEffects(FireworkEffect.builder().withColor(Color.PURPLE).with(FireworkEffect.Type.BALL_LARGE).withFlicker().build());
        data.setPower(1);
        firework.setFireworkMeta(data);
        player.sendMessage(CC.translate("&4&lBleed &7| &fYou have received 3x " + Foxtrot.getInstance().getAbilityHandler().getAbilities().get(p).getName() + "&r&f's."));
        player.sendMessage(CC.translate("&4&lBleed &7| &fYou have received 3x " + Foxtrot.getInstance().getAbilityHandler().getAbilities().get(pp).getName() + "&r&f's."));
        player.sendMessage(CC.translate("&4&lBleed &7| &fYou have received 3x " + Foxtrot.getInstance().getAbilityHandler().getAbilities().get(ppp).getName() + "&r&f's."));
    }


    private boolean isSimilarTo(ItemStack item, ItemStack compareTo){

        boolean hasItemMeta = item.hasItemMeta();
        boolean compareToHasItemMeta = compareTo.hasItemMeta();

        boolean itemHasDisplayName = false;
        boolean compareToHasDisplayName = false;

        String itemDisplayName = "";

        String compareToDisplayName = "";

        List<String> itemLore = new ArrayList<>();
        List<String> compareToLore = new ArrayList<>();

        if(hasItemMeta && compareToHasItemMeta){
            itemHasDisplayName = item.getItemMeta().hasDisplayName();
            compareToHasDisplayName = item.getItemMeta().hasDisplayName();
            if(itemHasDisplayName) itemDisplayName = item.getItemMeta().getDisplayName();
            if(compareToHasDisplayName) compareToDisplayName = compareTo.getItemMeta().getDisplayName();
            if(item.getItemMeta().hasLore()) itemLore = item.getItemMeta().getLore();
            if(compareTo.getItemMeta().hasLore()) compareToLore = compareTo.getItemMeta().getLore();
        }

        return item.getType() == compareTo.getType() &&
                hasItemMeta &&
                itemHasDisplayName &&
                compareToHasDisplayName &&
                itemDisplayName.equals(compareToDisplayName) && itemLore.containsAll(compareToLore);
    }

}

package net.frozenorb.foxtrot.extras.ability.packages;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Sound;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityPackageHandler implements Listener {
    @EventHandler
    public void onAbilityPackage(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!player.getInventory().getItemInMainHand().equals(Foxtrot.getInstance().getAbilityPackage().getPackage()) || player.getInventory().getItemInOffHand().equals(Foxtrot.getInstance().getAbilityPackage().getPackage()))  return;
        ItemStack partnerPackage = null;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item.equals(Foxtrot.getInstance().getAbilityPackage().getPackage())) {
                partnerPackage = item;
                break;
            }
        }
        if (partnerPackage == null) return;
        partnerPackage.setAmount(partnerPackage.getAmount() - 1);

        int inventorySize = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            inventorySize++;
        }
        if (inventorySize >= 33) {
            player.sendMessage("§cYou do not have enough inventory space to open the package.");
            return;
        }
        int p = Foxtrot.RANDOM.nextInt(0, Foxtrot.getInstance().getAbilityHandler().getAbilities().size());
        int pp = Foxtrot.RANDOM.nextInt(0, Foxtrot.getInstance().getAbilityHandler().getAbilities().size());
        int ppp = Foxtrot.RANDOM.nextInt(0, Foxtrot.getInstance().getAbilityHandler().getAbilities().size());
        for (int i = 0; i <= 3; i++) {
            player.getInventory().addItem(Foxtrot.getInstance().getAbilityHandler().getAbilities().get(pp).getItemStack());
            player.getInventory().addItem(Foxtrot.getInstance().getAbilityHandler().getAbilities().get(ppp).getItemStack());
            player.getInventory().addItem(Foxtrot.getInstance().getAbilityHandler().getAbilities().get(p).getItemStack());
        }


        player.sendMessage(CC.translate("&4&lBleed &7| &fYou have received 3x " + Foxtrot.getInstance().getAbilityHandler().getAbilities().get(p).getName() + "&r&f's."));
        player.sendMessage(CC.translate("&4&lBleed &7| &fYou have received 3x " + Foxtrot.getInstance().getAbilityHandler().getAbilities().get(pp).getName() + "&r&f's."));
        player.sendMessage(CC.translate("&4&lBleed &7| &fYou have received 3x " + Foxtrot.getInstance().getAbilityHandler().getAbilities().get(ppp).getName() + "&r&f's."));
    }

}

package net.frozenorb.foxtrot.gameplay.ability.partnerpackages;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.interact.type.NinjaStar;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class PartnerPackageHandler implements Listener {

    public ItemStack PACKAGE_ITEM = ItemBuilder.of(Material.ENDER_CHEST)
            .name("&d&lAbility Package")
            .addToLore("&7Right click to receive special partner items.").build();

    @EventHandler
    public void open(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!isSimilarTo(player.getItemInHand(), PACKAGE_ITEM)) return;

            event.setCancelled(true);

            if (player.getItemInHand().getAmount() == 1){
                player.setItemInHand(null);
            } else {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            }

            List<Ability> abilities = new ArrayList<>(HCF.getInstance().getAbilityHandler().getAllAbilities());
            abilities.remove(new NinjaStar());

            for (int i = 0; i < 2; i++){
                ItemStack partnerItem = abilities.get(HCF.RANDOM.nextInt(abilities.size())).getItemStack();

                player.getInventory().addItem(ItemBuilder.copyOf(partnerItem).amount(3).build());
            }

            Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
            FireworkMeta data = firework.getFireworkMeta();

            data.addEffects(FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).withFlicker().build());
            data.setPower(1);
            firework.setMetadata("nodamage",  new FixedMetadataValue(HCF.getInstance(), true));
            firework.setFireworkMeta(data);
        }
    }

    @EventHandler
    public void antiFireworkDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework fw) {
            if (fw.hasMetadata("nodamage")) {
                e.setCancelled(true);
            }
        }
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

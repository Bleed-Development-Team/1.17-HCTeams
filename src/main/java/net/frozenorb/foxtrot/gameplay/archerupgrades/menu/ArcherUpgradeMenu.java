package net.frozenorb.foxtrot.gameplay.archerupgrades.menu;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.archerupgrades.ArcherUpgrade;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.PlayerInventory;

public class ArcherUpgradeMenu extends Menu {

    public ArcherUpgradeMenu(Player player) {
        super(player, "Archer Upgrades", 27);
    }

    @Override
    public void tick() {
        int i = 10;
        for (ArcherUpgrade archerUpgrade : HCF.getInstance().getArcherUpgradeHandler().archerUpgrades){
            String effectName = archerUpgrade.getEffect().getType().getName();
            String formattedName = effectName.substring(0, 1).toUpperCase() + effectName.substring(1).toLowerCase();

            buttons[i] = new Button(ItemBuilder.of(Material.LEATHER_HELMET)
                    .dye(archerUpgrade.getDyeColor())
                    .name(archerUpgrade.getName())
                    .addToLore("", "&fEvery shot has a &e25% &fchance of giving ",
                            archerUpgrade.getColor() +
                            formattedName + " II &fto the target.", "", "&fGems Cost: " + archerUpgrade.getColor() + archerUpgrade.getCost(), "", "&7Click to apply this upgrade.")
                    .enchant(Enchantment.DURABILITY, 1)
                    .flag(ItemFlag.HIDE_ENCHANTS).build())
                    .setClickAction(event -> {
                        event.setCancelled(true);

                        if (HCF.getInstance().getGemsMap().getGems(getPlayer().getUniqueId()) < archerUpgrade.getCost()){
                            getPlayer().sendMessage(CC.translate("&cYou do not have enough gems to purchase this upgrade."));
                            return;
                        }

                        if (!archerUpgrade.isWearingSet(getPlayer().getInventory())) {
                            getPlayer().sendMessage(CC.translate("&cYou must be wearing a leather set."));
                            return;
                        }

                        PlayerInventory armor = getPlayer().getInventory();

                        armor.setHelmet(ItemBuilder.copyOf(armor.getHelmet()).dye(archerUpgrade.getDyeColor()).build());
                        armor.setChestplate(ItemBuilder.copyOf(armor.getChestplate()).dye(archerUpgrade.getDyeColor()).build());
                        armor.setLeggings(ItemBuilder.copyOf(armor.getLeggings()).dye(archerUpgrade.getDyeColor()).build());
                        armor.setBoots(ItemBuilder.copyOf(armor.getBoots()).dye(archerUpgrade.getDyeColor()).build());

                        getPlayer().sendMessage(CC.translate("&aYou have successfully bought this upgrade."));
                        HCF.getInstance().getGemsMap().removeGems(getPlayer().getUniqueId(), archerUpgrade.getCost());
                    });

            i += 2;
        }


    }
}

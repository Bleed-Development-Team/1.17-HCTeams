package net.frozenorb.foxtrot.team.upgrades.menu;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrades.Upgrade;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class UpgradesMenu extends Menu {

    private final Team team;

    public UpgradesMenu(Player player, Team team) {
        super(player, "Upgrades", 27);
        this.team = team;
    }

    @Override
    public void tick() {
        for(int i = 0; i < 27; i++){
            try {
                buttons[i] = new Button(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                        .setDisplayName(" ")
                        .setClickAction(event -> event.setCancelled(true));
            } catch (IndexOutOfBoundsException ex){
                return;
            }
        }

        buttons[10] = new Button(Material.AIR);
        buttons[11] = new Button(Material.AIR);
        buttons[16] = new Button(Material.AIR);
        buttons[15] = new Button(Material.AIR);
        buttons[13] = new Button(Material.AIR);

        buttons[12] = new Button(ItemBuilder.of(Material.BLAZE_POWDER).name("&c&lStrength &fUpgrade")
                .addToLore("", "&fUnlock this perk to gain strength", "&fonly on your claim!", "", "&b&l| &fCost: &b" + Upgrade.STRENGTH.getCost(),
                        "&b&l| &fUnlocked: " + (getUnlocked(Upgrade.STRENGTH) ? "&aYes" : "&cNo"), "").build())
                .setClickAction(event -> {
                    event.setCancelled(true);
                    if (getUnlocked(Upgrade.STRENGTH)) return;
                    if (HCF.getInstance().getGemsMap().getGems(getPlayer().getUniqueId()) < Upgrade.STRENGTH.getCost()){
                        getPlayer().sendMessage(CC.translate("&cYou do not have enough gems to purchase this upgrade."));
                        return;
                    }


                    HCF.getInstance().getGemsMap().setGems(getPlayer().getUniqueId(), HCF.getInstance().getGemsMap().getGems(getPlayer().getUniqueId()) - Upgrade.STRENGTH.getCost());
                    getPlayer().sendMessage(CC.translate("&aSuccesfully purchased the Strength upgrade."));

                    team.getFactionUpgrades().add(Upgrade.STRENGTH);
                    team.flagForSave();

                    getPlayer().closeInventory();
                    new UpgradesMenu(getPlayer(), team).updateMenu();
                });
        buttons[14] = new Button(ItemBuilder.of(Material.IRON_INGOT).name("&e&lResistance &fUpgrade")
                .addToLore("", "&fUnlock this perk to gain resistance", "&fonly on your claim!", "", "&b&l| &fCost: &b" + Upgrade.RESISTANCE.getCost(),
                        "&b&l| &fUnlocked: " + (getUnlocked(Upgrade.RESISTANCE) ? "&aYes" : "&cNo"), "").build())
                .setClickAction(event -> {
                    event.setCancelled(true);
                    if (getUnlocked(Upgrade.RESISTANCE)) return;
                    if (HCF.getInstance().getGemsMap().getGems(getPlayer().getUniqueId()) < Upgrade.RESISTANCE.getCost()){
                        getPlayer().sendMessage(CC.translate("&cYou do not have enough gems to purchase this upgrade."));
                        return;
                    }


                    HCF.getInstance().getGemsMap().setGems(getPlayer().getUniqueId(), HCF.getInstance().getGemsMap().getGems(getPlayer().getUniqueId()) - Upgrade.RESISTANCE.getCost());
                    getPlayer().sendMessage(CC.translate("&aSuccesfully purchased the Resistance upgrade."));

                    team.getFactionUpgrades().add(Upgrade.RESISTANCE);
                    team.flagForSave();

                    getPlayer().closeInventory();
                    new UpgradesMenu(getPlayer(), team).updateMenu();
                });
    }

    private boolean getUnlocked(Upgrade upgrade){
        return team.getFactionUpgrades().stream().anyMatch(e -> e.getEffects() == upgrade.getEffects());
    }
}

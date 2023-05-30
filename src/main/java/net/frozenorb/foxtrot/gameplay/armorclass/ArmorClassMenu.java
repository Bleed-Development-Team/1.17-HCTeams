package net.frozenorb.foxtrot.gameplay.armorclass;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ArmorClassMenu extends Menu {

    public ArmorClassMenu(Player player) {
        super(player, "Armor Classes", 27);
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

        buttons[4] = new Button(Material.BOOK)
                .setDisplayName("&9&lArmor Classes")
                .setLore(new String[]{
                        "",
                        "&bWhat are Armor Classes?",
                        "&9| &fEach armor class provides unique traits.",
                        "&9| &fSuit up to enhance your play-style.",
                        "",
                        "&7Hover over each armor class to view it's use."
                }).setClickAction(event -> event.setCancelled(true));

        buttons[11] = new Button(Material.DIAMOND_PICKAXE)
                .setDisplayName("&3&lMiner")
                .setLore(new String[]{
                        "",
                        "&7This class is the best to collect",
                        "&7resources of any kind.",
                        "",
                        "&3&lEffects",
                        "&3| &fFire Resistance I",
                        "&3| &fNight Vision I",
                        "&3| &fHaste II",
                        "&3| &fInvisibility (Under Y: 20)",
                        "",
                        "&3&lAbilities",
                        "&3| &fUnlock access to rewards as",
                        "&3| &fYou reach milestones of diamonds mined.",
                }).setClickAction(event -> event.setCancelled(true));

        buttons[12] = new Button(Material.GOLDEN_HELMET)
                .setDisplayName("&6&lMage")
                .setLore(new String[]{
                        "",
                        "&7Give your enemies special effects",
                        "&7that debuff them in fights.",
                        "",
                        "&6&lEffects",
                        "&6| &fSpeed II",
                        "&6| &fRegeneration I",
                        "&6| &fResistance II",
                        "",
                        "&6&lAbilities",
                        "&6| &fCast spells to give your enemies",
                        "&6| &fdebuffs in fights.",
                }).setClickAction(event -> event.setCancelled(true));

        buttons[13] = new Button(Material.LEATHER_CHESTPLATE)
                .setDisplayName("&a&lArcher")
                .setLore(new String[]{
                        "",
                        "&7Mark your enemies to deal more",
                        "&7damage to them.",
                        "",
                        "&a&lEffects",
                        "&a| &fSpeed III",
                        "&a| &fResistance II",
                        "&a| &fClickable Boosts",
                        "",
                        "&a&lAbilities",
                        "&a| &fDeal increased damage against your",
                        "&a| &fenemies when marked.",
                }).setClickAction(event -> event.setCancelled(true));

        buttons[14] = new Button(Material.GOLDEN_CHESTPLATE)
                .setDisplayName("&e&lBard")
                .setLore(new String[]{
                        "",
                        "&7Cast spells that give your",
                        "&7teammates special effects.",
                        "",
                        "&e&lEffects",
                        "&e| &fSpeed II",
                        "&e| &fRegeneration I",
                        "&e| &fResistance II",
                        "",
                        "&e&lAbilities",
                        "&e| &fHelp your teammates in battle",
                        "&e| &fby giving them certain effects.",
                }).setClickAction(event -> event.setCancelled(true));

        buttons[15] = new Button(Material.CHAINMAIL_CHESTPLATE)
                .setDisplayName("&b&lRogue")
                .setLore(new String[]{
                        "",
                        "&7This class helps your team in battle",
                        "&7by dealing large amounts of damage.",
                        "",
                        "&b&lEffects",
                        "&b| &fSpeed III",
                        "&b| &fJump Boost II",
                        "&b| &fResistance I",
                        "&b| &fClickable Boosts",
                        "",
                        "&b&lAbilities",
                        "&b| &fBackstab your enemies with golden swords",
                        "&b| &fto deal large amounts of damage.",
                }).setClickAction(event -> event.setCancelled(true));

        buttons[10] = new Button(Material.AIR);
        buttons[16] = new Button(Material.AIR);
    }
}

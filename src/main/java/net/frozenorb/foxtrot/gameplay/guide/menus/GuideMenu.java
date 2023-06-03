package net.frozenorb.foxtrot.gameplay.guide.menus;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.gameplay.ability.menu.AbilitiesMenu;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassMenu;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class GuideMenu extends Menu {
    public GuideMenu(Player player) {
        super(player, "Guide", 45);
    }

    @Override
    public void tick() {
        this.buttons[11] = new Button(ItemBuilder.of(Material.BREWING_STAND).name(CC.translate("&3&lPotion Makers")).addToLore("", "&fPotion makers provide an easy way to", "&freceive health potions.", "", "&fBy right clicking the potion maker", "&fyou must wait &35 &fseconds and you will receive", "&33 &fhealth potions.", "", "&fThe potion maker can be crafted by", "&fputting &cGlistering Melons&f, &eGlass&f, &6Glowstone Dust&f, and &cNether Wart&f.", "", "&fThis item has a cooldown of &35 &fseconds.").build());
        this.buttons[13] = new Button(ItemBuilder.of(Material.PLAYER_HEAD).name("&b&lFrozen &7| &fHCF").owningPlayer("lolitsalex").build());
        this.buttons[15] = new Button(ItemBuilder.of(Material.DIAMOND_SWORD).name(CC.translate("&b&lKits")).addToLore("", "&fKits provide an easy to way to get extra pots", "&fand armor.", "", "&fSeeing this kits can be as easy as just doing &b/kits&f.", "&fAlthough many kits are rank locked, you can still use", "&fthe &a&lFree &fKit.", "", "&eLeft click to see all kits.")
                .build())
                .setClickAction(event -> {

                });

        this.buttons[29] = new Button(ItemBuilder.of(Material.GOLDEN_CHESTPLATE).enchant(Enchantment.DURABILITY, 1).flag(ItemFlag.HIDE_ENCHANTS).name(CC.translate("&a&lArmor Classes"))
                .addToLore("", "&fArmor classes provide a unique way to PvP.", "&fEach armor class has its own buffs that can", "&fpositively affect teammates or negatively affect other players", "", "&eLeft click here to find our more.").build())
                .setClickAction(event -> {
                    event.setCancelled(true);
                    new ArmorClassMenu(getPlayer()).updateMenu();
                });
        this.buttons[31] = new Button(ItemBuilder.of(Material.BONE).name(CC.translate("&d&lPartner Items")).addToLore("", "&d&lPartner &fitems give special abilities and positive traits.", "&fThese items could help you in 1by1s or", "&fregular PvP scenarios.", "", "&eLeft click to see our abilities!").build())
                .setClickAction(event -> {
                    event.setCancelled(true);
                    new AbilitiesMenu(getPlayer()).updateMenu();
                });
        this.buttons[33] = new Button(ItemBuilder.of(Material.GOLD_NUGGET).name(CC.translate("&9&lKOTHS"))
                .addToLore("", "&fKOTHs are special events that occur throughout the day.", "&fThese KOTHs are &95 &fminutes long and when", "&fcaptured, they give your faction &925 &fpoints and", "&f1 &9KOTH Key&f. KOTH keys can be opened", "&fat &aSpawn &fand provide protection 3 armor.", "", "&eLeft click to see our KOTH schedules!").build())
                .setClickAction(event -> {
                    event.setCancelled(true);
                    getPlayer().closeInventory();
                    getPlayer().chat("/koth schedule");
                });

    }
}

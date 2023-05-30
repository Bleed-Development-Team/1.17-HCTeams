package net.frozenorb.foxtrot.gameplay.clickable;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.clickable.listener.ClickableListener;
import net.frozenorb.foxtrot.gameplay.clickable.type.GemsPack;
import net.frozenorb.foxtrot.gameplay.clickable.type.RandomPartnerItem;
import net.frozenorb.foxtrot.gameplay.clickable.type.RankPouch;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ClickableItemHandler {

    public List<ClickableItem> clickableItems = new ArrayList<>();

    public ClickableItemHandler(){
        clickableItems.add(new GemsPack());
        clickableItems.add(new RandomPartnerItem());

        clickableItems.add(new RankPouch(ItemBuilder.of(Material.BOOK).name("&b&lIce &fRank Pouch").addToLore("&7Right click to redeem the &b&lIce &7rank.").build(), "Ice"));
        clickableItems.add(new RankPouch(ItemBuilder.of(Material.BOOK).name("&9&lFrozen &fRank Pouch").addToLore("&7Right click to redeem the &9&lFrozen &7rank.").build(), "Frozen"));
        clickableItems.add(new RankPouch(ItemBuilder.of(Material.BOOK).name("&3&lGlacial&9&l+ &fRank Pouch+").addToLore("&7Right click to redeem the &3&lGlacial&9&l+ &7rank.").build(), "Glacial+"));
        clickableItems.add(new RankPouch(ItemBuilder.of(Material.BOOK).name("&3&lGlacial &fRank Pouch").addToLore("&7Right click to redeem the &3&lGlacial &7rank.").build(), "Glacial"));
        clickableItems.add(new RankPouch(ItemBuilder.of(Material.BOOK).name("&f&lCloud &fRank Pouch").addToLore("&7Right click to redeem the &f&lCloud &7rank.").build(), "Cloud"));
        clickableItems.add(new RankPouch(ItemBuilder.of(Material.BOOK).name("&9&lBlizzard &fRank Pouch").addToLore("&7Right click to redeem the &9&lBlizzard &7rank.").build(), "Blizzard"));

        Bukkit.getPluginManager().registerEvents(new ClickableListener(this), HCF.getInstance());
    }

    public ClickableItem getClickableItem(ItemStack itemStack){
        ClickableItem clickableItem = null;
        for (ClickableItem item : this.clickableItems){
            if (item.getItemStack().isSimilar(itemStack)){
                clickableItem = item;
                break;
            }
        }

        return clickableItem;
    }

    public ClickableItem getClickableItem(String name){
        ClickableItem clickableItem = null;

        for (ClickableItem item : this.clickableItems){
            if (item.getID().equalsIgnoreCase(name)){
                clickableItem = item;
                break;
            }
        }

        return clickableItem;
    }

}

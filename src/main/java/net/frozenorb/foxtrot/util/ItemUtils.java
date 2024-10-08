package net.frozenorb.foxtrot.util;


import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.beans.ConstructorProperties;
import java.util.*;

public class ItemUtils {
    private static final Map<String, ItemData> NAME_MAP = new HashMap<String, ItemData>();

    public static void load() {
        NAME_MAP.clear();
    }

    public static void setDisplayName(ItemStack itemStack, String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
    }

    public static ItemBuilder builder(Material type) {
        return new ItemBuilder(type);
    }

    public static ItemStack get(String input, int amount) {
        ItemStack item = ItemUtils.get(input);
        if (item != null) {
            item.setAmount(amount);
        }
        return item;
    }

    public static ItemStack get(String input) {
        if (NumberUtils.isInteger(input = input.toLowerCase().replace(" ", ""))) {
            return new ItemStack(Objects.requireNonNull(Material.getMaterial(String.valueOf(Integer.parseInt(input)))));
        }
        if (input.contains(":")) {
            if (NumberUtils.isShort(input.split(":")[1])) {
                if (NumberUtils.isInteger(input.split(":")[0])) {
                    return new ItemStack(Objects.requireNonNull(Material.getMaterial(String.valueOf(Integer.parseInt(input.split(":")[0])))), 1, Short.parseShort(input.split(":")[1]));
                }
                if (!NAME_MAP.containsKey(input.split(":")[0].toLowerCase())) {
                    return null;
                }
                ItemData data = NAME_MAP.get(input.split(":")[0].toLowerCase());
                return new ItemStack(data.getMaterial(), 1, Short.parseShort(input.split(":")[1]));
            }
            return null;
        }
        if (!NAME_MAP.containsKey(input)) {
            return null;
        }
        return NAME_MAP.get(input).toItemStack();
    }

    public static class ItemData {
        private final Material material;
        private final short data;


        public boolean matches(ItemStack item) {
            return item != null && item.getType() == this.material && item.getDurability() == this.data;
        }

        public ItemStack toItemStack() {
            return new ItemStack(this.material, 1, this.data);
        }

        public Material getMaterial() {
            return this.material;
        }

        public short getData() {
            return this.data;
        }

        @ConstructorProperties(value={"material", "data"})
        public ItemData(Material material, short data) {
            this.material = material;
            this.data = data;
        }
    }
    public static String getName(ItemStack item) {
        String name = CraftItemStack.asNMSCopy(item).toString();
        if (name.contains(".")) {
            name = WordUtils.capitalize((String)item.getType().toString().toLowerCase().replace("_", " "));
        }
        return name;
    }

    public static boolean isSimilarTo(ItemStack item, ItemStack compareTo){

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

    public static final class ItemBuilder {
        private Material type;
        private int amount = 1;
        private short data = 0;
        private String name;
        private List<String> lore = new ArrayList<>();
        private Map<Enchantment, Integer> enchantments = new HashMap<>();

        private ItemBuilder(Material type) {
            this.type = type;
        }

        public ItemBuilder type(Material type) {
            this.type = type;
            return this;
        }

        public ItemBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public ItemBuilder data(short data) {
            this.data = data;
            return this;
        }

        public ItemBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ItemBuilder addLore(String ... lore) {
            this.lore.addAll(Arrays.asList(lore));
            return this;
        }

        public ItemBuilder addLore(int index, String lore) {
            this.lore.set(index, lore);
            return this;
        }

        public ItemBuilder setLore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemBuilder enchant(Enchantment enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        public ItemBuilder unenchant(Enchantment enchantment) {
            this.enchantments.remove((Object)enchantment);
            return this;
        }

        public ItemStack build() {
            ItemStack item = new ItemStack(this.type, this.amount, this.data);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)this.name));
            ArrayList<String> finalLore = new ArrayList<String>();
            for (int index = 0; index < this.lore.size(); ++index) {
                if (this.lore.get(index) == null) {
                    finalLore.set(index, "");
                    continue;
                }
                finalLore.set(index, ChatColor.translateAlternateColorCodes((char)'&', (String)this.lore.get(index)));
            }
            meta.setLore(finalLore);
            for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
                item.addUnsafeEnchantment(entry.getKey(), entry.getValue().intValue());
            }
            item.setItemMeta(meta);
            return item;
        }
    }
}


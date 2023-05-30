package net.frozenorb.foxtrot.util;


import net.frozenorb.foxtrot.HCF;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class DeathbanUtils {

    public static void giveKit(Player player){
        player.getInventory().setHelmet(fetchArmor("helmet"));
        player.getInventory().setChestplate(fetchArmor("chestplate"));
        player.getInventory().setLeggings(fetchArmor("leggings"));
        player.getInventory().setBoots(fetchArmor("boots"));

        player.getInventory().addItem(ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).enchant(Enchantment.FIRE_ASPECT, 2).enchant(Enchantment.DURABILITY,3).build());
        player.getInventory().addItem(ItemBuilder.of(Material.ENDER_PEARL).amount(16).build());

        for (int i = 0; i < 36; i++) {
            ItemStack item = new ItemStack(Material.SPLASH_POTION, 1);

            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1000, 1), true);
            meta.setDisplayName(CC.translate("&fSplash Potion of Healing"));

            item.setItemMeta(meta);

            player.getInventory().addItem(item);
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
    }

    public static void putOutOfDeathban(Player player, String outcome){
        player.teleport(HCF.getInstance().getServerHandler().getSpawnLocation());
        player.getInventory().clear();

        HCF.getInstance().getDeathbanMap().revive(player.getUniqueId());


        Bukkit.getScheduler().scheduleSyncDelayedTask(HCF.getInstance(), () -> {
            HCF.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), 30 * 60);//moved inside here due to occasional CME maybe this will fix?
        }, 20L);

    }

    public static void teleportToDeathban(Player player){
        player.teleport(new Location(
                Bukkit.getWorld(HCF.getInstance().getConfig().getString("deathban-arena.world")),
                HCF.getInstance().getConfig().getDouble("deathban-arena.x"),
                HCF.getInstance().getConfig().getDouble("deathban-arena.y"),
                HCF.getInstance().getConfig().getDouble("deathban-arena.z"),
                (float) HCF.getInstance().getConfig().getDouble("deathban-arena.yaw"),
                (float) HCF.getInstance().getConfig().getDouble("deathban-arena.pitch")
        ));

        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&cYou have been sent to the &c&lDeathban &carena."));
        player.sendMessage(CC.translate("&c"));
        player.sendMessage(CC.translate("&cClick the sign to revive yourself if you have lives.\nHop into the arena to remove some remaining time!"));
        player.sendMessage(CC.translate(""));
    }

    public static void setDeathbanArea(Location location){
        HCF.getInstance().getConfig().set("deathban-arean.world", location.getWorld().getName());
        HCF.getInstance().getConfig().set("deathban.x", location.getX());
        HCF.getInstance().getConfig().set("deathban.y", location.getY());
        HCF.getInstance().getConfig().set("deathban-arena.z", location.getZ());
        HCF.getInstance().getConfig().set("deathban-arena.yaw", location.getYaw());
        HCF.getInstance().getConfig().set("deathban-arena.pitch", location.getPitch());
        HCF.getInstance().saveConfig();
    }


    private static ItemStack fetchArmor(String type){
        return switch (type) {
            case "helmet" -> ItemBuilder.of(Material.DIAMOND_HELMET)
                    .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .enchant(Enchantment.DURABILITY, 3)
                    .name("&c&lDeathban Helmet").build();
            case "chestplate" -> ItemBuilder.of(Material.DIAMOND_CHESTPLATE)
                    .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .enchant(Enchantment.DURABILITY, 3)
                    .name("&c&lDeathban Chestplate").build();
            case "leggings" -> ItemBuilder.of(Material.DIAMOND_LEGGINGS)
                    .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .enchant(Enchantment.DURABILITY, 3)
                    .name("&c&lDeathban Leggings").build();
            case "boots" -> ItemBuilder.of(Material.DIAMOND_BOOTS)
                    .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .enchant(Enchantment.DURABILITY, 3)
                    .name("&c&lDeathban Boots").build();
            default -> null;
        };
    }



}
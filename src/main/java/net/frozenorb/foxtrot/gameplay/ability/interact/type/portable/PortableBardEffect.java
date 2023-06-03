package net.frozenorb.foxtrot.gameplay.ability.interact.type.portable;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.ItemBuilder;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.minecraft.server.v1_16_R3.AxisAlignedBB;
import net.minecraft.server.v1_16_R3.Block;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;


import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PortableBardEffect extends InteractAbility {

    public String id;
    public String name;
    public int cooldown;
    public Material material;
    public String color;
    public PotionEffect potionEffect;

    public PortableBardEffect(String id, String name, int cooldown, Material material, String color, PotionEffect potionEffect){
        this.id = id;
        this.name = name;
        this.cooldown = cooldown;
        this.material = material;
        this.color = color;
        this.potionEffect = potionEffect;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(material).name(name).addToLore("&a* Team Members")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        player.addPotionEffect(potionEffect);
        sendMessage(player, "&fYou were given " + getName() + " &ffor " + getColor() + TimeUtils.formatIntoDetailedString(potionEffect.getDuration() / 20) + "&f.");

        final Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team != null) {
            player.getNearbyEntities(25,25,25).stream().filter(it -> it instanceof Player && team.isMember(it.getUniqueId()))
                    .forEach(it -> ((Player) it).addPotionEffect(potionEffect));
        }

        use(player);
    }
}
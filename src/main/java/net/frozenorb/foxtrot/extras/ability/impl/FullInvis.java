package net.frozenorb.foxtrot.extras.ability.impl;

import com.mojang.datafixers.util.Pair;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.item.Items;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Cooldown;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class FullInvis implements Listener {
    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (player.getItemInHand() == Items.getFullInvis()){
            if (Cooldown.isOnCooldown("invis", player)){
                player.sendMessage(CC.translate("&cYou cannot use this for another &c&l" + Cooldown.getCooldownString(player, "invis") + "&c!"));
                //event.setCancelled(true);
                return;
            }
            List<Pair<EnumItemSlot, ItemStack>> armor_list = new ArrayList<>();
            armor_list.add(Pair.of(EnumItemSlot.a, null));
            armor_list.add(Pair.of(EnumItemSlot.b, null));
            armor_list.add(Pair.of(EnumItemSlot.c, null));
            armor_list.add(Pair.of(EnumItemSlot.d, null));
            Bukkit.getOnlinePlayers().stream().filter(p -> Foxtrot.getInstance().getTeamHandler().getTeam(p) != Foxtrot.getInstance().getTeamHandler().getTeam(player)).forEach(p -> {
                PacketPlayOutEntityEquipment armor = new PacketPlayOutEntityEquipment(p.getEntityId(), armor_list);
                ((CraftPlayer) p).getHandle().b.sendPacket(armor);
            });
            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                if (!player.isOnline()) {
                    return;
                }
                PlayerInventory i = player.getInventory();
                armor_list.clear();
                armor_list.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(i.getHelmet())));
                armor_list.add(Pair.of(EnumItemSlot.b, CraftItemStack.asNMSCopy(i.getChestplate())));
                armor_list.add(Pair.of(EnumItemSlot.c, CraftItemStack.asNMSCopy(i.getLeggings())));
                armor_list.add(Pair.of(EnumItemSlot.d, CraftItemStack.asNMSCopy(i.getBoots())));
                for (Player p : Bukkit.getOnlinePlayers()){
                    PacketPlayOutEntityEquipment armor = new PacketPlayOutEntityEquipment(p.getEntityId(), armor_list);
                    ((CraftPlayer) p).getHandle().b.sendPacket(armor);
                }
            }, 5 * 60 * 20);
        }
    }
}

package net.frozenorb.foxtrot.gameplay.ability.interact.type;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class AntiTrapper extends InteractAbility {

    private int HITS_TO_BREAK = 10;
    private int NEARBY_RADIUS = 8;
    private int DURATION = 15;

    public AntiTrapper(){
        Cooldown.createCooldown("trapper-eff");
    }

    @Override
    public String getID() {
        return "antitrapper";
    }

    @Override
    public String getName() {
        return "&3&lAnti Trapper";
    }

    @Override
    public int getCooldown() {
        return 60 * 3;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemBuilder.of(Material.BEACON)
                .name(getName())
                .addToLore("&7Place this block to prevent people", "&7in a " + NEARBY_RADIUS + " &7block radius from placing blocks.")
                .enchant(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public String getColor() {
        return "&3";
    }

    @Override
    public void handle(PlayerInteractEvent event, Player player) {
        event.setCancelled(true);
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if (!ItemUtils.isSimilarTo(player.getItemInHand(), getItemStack())) return;

        Team ownerFac = LandBoard.getInstance().getTeam(player.getLocation());

        if (ownerFac != null && ownerFac.getOwner() != null){
            event.setCancelled(false);

            Block block = event.getBlockPlaced();
            World world = Bukkit.getWorld(HCF.world);
            Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());

            block.setMetadata("anti-trapper", new FixedMetadataValue(HCF.getInstance(), HITS_TO_BREAK));

            world.getNearbyEntities(block.getLocation(), NEARBY_RADIUS, NEARBY_RADIUS, NEARBY_RADIUS)
                    .stream().filter(it -> it instanceof Player)
                    .forEach(it -> {
                                if (team == null) {
                                    Cooldown.addCooldown("trapper-eff", (Player) it, DURATION);
                                    it.sendMessage(CC.translate("&cYou were affected by an AntiTrapper. You are not able to place blocks for " + DURATION + " &cseconds."));
                                } else if (!team.isMember(it.getUniqueId())) {
                                    Cooldown.addCooldown("trapper-eff", (Player) it, DURATION);
                                    it.sendMessage(CC.translate("&cYou were affected by an AntiTrapper. You are not able to place blocks for " + DURATION + " &cseconds."));
                                }
                            });

            Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                if (block.getType() == Material.AIR) return;

                block.setType(Material.AIR);

                player.sendMessage(CC.translate("&cYour Anti Trapper has been broken!"));
            }, 20L * DURATION);
        } else {
            player.sendMessage(CC.translate("&cYou are only allowed to place this on someone else's claim!"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();

        if (block.hasMetadata("anti-trapper")){
            Team team = HCF.getInstance().getTeamHandler().getTeam(event.getPlayer());

            if (team != null && team.isMember(event.getPlayer().getUniqueId())) return;

            int blocksLeft = block.getMetadata("anti-trapper").get(0).asInt();

            if (blocksLeft == 1){
                World world = Bukkit.getWorld(HCF.world);

                world.getNearbyEntities(block.getLocation(), NEARBY_RADIUS, NEARBY_RADIUS, NEARBY_RADIUS)
                        .stream().filter(it -> it instanceof Player)
                        .forEach(it -> {
                            if (Cooldown.isOnCooldown("trapper-eff", (Player) it)){
                                Cooldown.removeCooldown("trapper-eff", (Player) it);
                            }
                        });

                block.setType(Material.AIR);
                event.getPlayer().sendMessage(CC.translate("&6You have broke the Anti Trapper."));

                return;
            }

            block.setMetadata("anti-trapper", new FixedMetadataValue(HCF.getInstance(), blocksLeft - 1));
            blocksLeft -= 1;
            event.getPlayer().sendMessage(CC.translate("&6You have to hit this beacon &f" + blocksLeft + " &6times to destroy it!"));
        }
    }

    @EventHandler
    public void preventBlock(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if (Cooldown.isOnCooldown("trapper-eff", player)){
            event.setCancelled(true);

            player.sendMessage(CC.translate("&cYou are currently under the Anti Trapper effect. You will be under this effect for &l" + TimeUtils.formatIntoDetailedString(Cooldown.getCooldownForPlayerInt("trapper-eff", player)) + "&c!"));
        }
    }
}

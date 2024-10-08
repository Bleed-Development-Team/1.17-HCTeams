package net.frozenorb.foxtrot.server.listener.impl;

import com.mojang.authlib.GameProfile;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.commands.op.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.staff.LastInvCommand;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;

public class CombatLoggerListener implements Listener {

    public static final String COMBAT_LOGGER_METADATA = "CombatLogger";
    @Getter private Set<Entity> combatLoggers = new HashSet<>();
    private final List<UUID> players = new ArrayList<>();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
        		boolean isKitMap = HCF.getInstance().getMapHandler().isKitMap() || HCF.getInstance().getServerHandler().isVeltKitMap();
            combatLoggers.remove(event.getEntity());
            CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

            if (!metadata.playerName.equals(event.getEntity().getCustomName().substring(2))) {
                HCF.getInstance().getLogger().warning("Combat logger name doesn't match metadata for " + metadata.playerName + " (" + event.getEntity().getCustomName().substring(2) + ")");
            }

            HCF.getInstance().getLogger().info(metadata.playerName + "'s combat logger at (" + event.getEntity().getLocation().getBlockX() + ", " + event.getEntity().getLocation().getBlockY() + ", " + event.getEntity().getLocation().getBlockZ() + ") died.");

            // Deathban the player
            HCF.getInstance().getDeathbanMap().deathban(metadata.playerUUID, metadata.deathBanTime);

            // Increment players deaths
            int deaths = HCF.getInstance().getDeathsMap().getDeaths(metadata.playerUUID);
            HCF.getInstance().getDeathsMap().setDeaths(metadata.playerUUID, deaths + 1);

            players.add(metadata.playerUUID);

            Team team = HCF.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

            // Take away DTR.
            if (team != null) {
                team.playerDeath(metadata.playerName, HCF.getInstance().getServerHandler().getDTRLoss(event.getEntity().getLocation()), event.getEntity().getKiller());
            }

            // Drop the player's items.
            for (ItemStack item : metadata.contents) {
                event.getDrops().add(item);
            }
            for (ItemStack item : metadata.armor) {
                event.getDrops().add(item);
            }


            // store the death amount -- we'll use this later on.
            int victimKills = HCF.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getUniqueId()).getKills();

            if (event.getEntity().getKiller() != null) {
                // give them a kill
                HCF.getInstance().getKillsMap().setKills(event.getEntity().getKiller().getUniqueId(), HCF.getInstance().getKillsMap().getKills(event.getEntity().getKiller().getUniqueId()) + 1);

                HCF.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getKiller()).addKill();

                int attacker = HCF.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getKiller().getUniqueId()).getKills();

                String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.DARK_RED + "[" + victimKills + "]" + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " was slain by " + ChatColor.RED + event.getEntity().getKiller().getName() + ChatColor.DARK_RED + "[" + attacker + "]" + ChatColor.YELLOW + ".";

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (HCF.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                        player.sendMessage(deathMessage);
                    } else {
                        if (HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null) {
                            continue;
                        }

                        if (HCF.getInstance().getTeamHandler().getTeam(metadata.playerUUID) != null
                                && HCF.getInstance().getTeamHandler().getTeam(metadata.playerUUID).equals(HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }

                        if (HCF.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller().getUniqueId()) != null
                                && HCF.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller().getUniqueId()).equals(HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }
                    }
                }

                // Add the death sign.

//              if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
//                  event.getDrops().add(Foxtrot.getInstance().getServerHandler().generateDeathSign(metadata.playerName, event.getEntity().getKiller().getKitName()));
//              }
            } else {
                int kills = HCF.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getUniqueId()).getKills();
                String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.DARK_RED + "[" + kills + "]" + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (HCF.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())){
                        player.sendMessage(deathMessage);
                    } else {
                        if (HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null) {
                            continue;
                        }

                        if (HCF.getInstance().getTeamHandler().getTeam(metadata.playerUUID) != null
                                && HCF.getInstance().getTeamHandler().getTeam(metadata.playerUUID).equals(HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }
                    }
                }
            }

            Player target = HCF.getInstance().getServer().getPlayer(metadata.playerUUID);

            if (target == null) {
                // Create an entity to load the player data

                MinecraftServer server = ((CraftServer) HCF.getInstance().getServer()).getServer();

                PlayerInteractManager playerInteractManager = new PlayerInteractManager(server.E());
                EntityPlayer entity = new EntityPlayer(server, server.E().getMinecraftWorld(), new GameProfile(metadata.playerUUID, metadata.playerName), playerInteractManager);
                target = entity.getBukkitEntity();

                if (target != null) {
                    target.loadData();
                }
            }

            if (target != null) {
                EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();

                target.getInventory().clear();
                target.getInventory().setArmorContents(null);
                humanTarget.getBukkitEntity().setHealth(0);

                spoofWebsiteData(target, event.getEntity().getKiller());
                target.saveData();
            }

            LastInvCommand.recordInventory(metadata.playerUUID, metadata.contents, metadata.armor);

            event.getEntity().remove();
        }
    }

    // Prevent trading with the logger.
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if (players.contains(player.getUniqueId())){
            player.getInventory().clear();

            player.teleport(HCF.getInstance().getServerHandler().getSpawnLocation());
            players.remove(player.getUniqueId());
        }
    }

    // Kill loggers when their chunk unloads
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata(COMBAT_LOGGER_METADATA) && !entity.isDead()) {
                entity.remove();
            }
        }
    }

    // Don't let the NPC go through portals
    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }
    }

    // Despawn the NPC when its owner joins.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Iterator<Entity> combatLoggerIterator = combatLoggers.iterator();

        while (combatLoggerIterator.hasNext()) {
            Villager villager = (Villager) combatLoggerIterator.next();

            if (villager.isCustomNameVisible() && ChatColor.stripColor(villager.getCustomName()).equals(event.getPlayer().getName())) {
                villager.remove();
                combatLoggerIterator.remove();
            }
        }
    }

    // Prevent combat logger friendly fire.
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            return;
        }

        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager != null) {
            CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

            if (DTRBitmask.SAFE_ZONE.appliesAt(damager.getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (HCF.getInstance().getPvPTimerMap().hasTimer(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            Team team = HCF.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

            if (team != null && team.isMember(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime());
        }
    }

    // Prevent combatloggers from activating a pressure plate
    @EventHandler
    public void onEntityPressurePlate(EntityInteractEvent event) {
        if (event.getBlock().getType() == Material.STONE_PRESSURE_PLATE && event.getEntity() instanceof Villager && event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true); // block is stone, entity is a combat tagged villager
        }
    }

    // Spawn the combat logger
    @EventHandler(priority=EventPriority.LOW) // So we run before Mod Suite code will.
    public void onPlayerQuit(PlayerQuitEvent event) {
        // If the player safe logged out
        if (event.getPlayer().hasMetadata("loggedout")) {
            event.getPlayer().removeMetadata("loggedout", HCF.getInstance());
            return;
        }

        if (event.getPlayer().hasMetadata("invisible") || event.getPlayer().hasMetadata("modmode")) {
            return;
        }

        // If the player is in spawn
        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            return;
        }

        // If they have a PvP timer.
        if (HCF.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            return;
        }

        // If they're dead.
        if (event.getPlayer().isDead()) {
            return;
        }

        // If they're frozen or the server is
        if (event.getPlayer().hasMetadata("frozen")) {
            return;
        }

        // If the player is below Y = 0 (aka in the void)
        if (event.getPlayer().getLocation().getBlockY() <= 0) {
            return;
        }

        if (CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(event.getPlayer().getUniqueId())) {
            return;
        }

        boolean spawnCombatLogger = false;

        for (Entity entity : event.getPlayer().getNearbyEntities(40, 40, 40)) {
            if (entity instanceof Player) {
                Player other = (Player) entity;

                if (other.hasMetadata("invisible")) {
                    continue;
                }

                Team otherTeam = HCF.getInstance().getTeamHandler().getTeam(other);
                Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(event.getPlayer());

                if (otherTeam != playerTeam || playerTeam == null) {
                    spawnCombatLogger = true;
                    break;
                }
            }
        }

        if (!event.getPlayer().isOnGround()) {
            spawnCombatLogger = true;
        }

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && !event.getPlayer().hasMetadata("invisible") && spawnCombatLogger && !event.getPlayer().isDead()) {
            HCF.getInstance().getLogger().info(event.getPlayer().getName() + " combat logged at (" + event.getPlayer().getLocation().getBlockX() + ", " + event.getPlayer().getLocation().getBlockY() + ", " + event.getPlayer().getLocation().getBlockZ() + ")");

            ItemStack[] armor = event.getPlayer().getInventory().getArmorContents();
            ItemStack[] inv = event.getPlayer().getInventory().getContents();

            final Villager villager = (Villager) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.VILLAGER);

            villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            //villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));

            if (event.getPlayer().hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
                    // have to use .equals() as PotionEffectType isn't an enum
                    if (potionEffect.getType().equals(PotionEffectType.FIRE_RESISTANCE)) {
                        villager.addPotionEffect(potionEffect);
                        break;
                    }
                }
            }

            CombatLoggerMetadata metadata = new CombatLoggerMetadata();

            metadata.playerName = event.getPlayer().getName();
            metadata.playerUUID = event.getPlayer().getUniqueId();
            //metadata.deathBanTime = Foxtrot.getInstance().getServerHandler().getDeathban(metadata.playerUUID, event.getPlayer().getLocation());
            metadata.contents = inv;
            metadata.armor = armor;

            villager.setMetadata(COMBAT_LOGGER_METADATA, new FixedMetadataValue(HCF.getInstance(), metadata));

            villager.setMaxHealth(calculateCombatLoggerHealth(event.getPlayer()));
            villager.setHealth(villager.getMaxHealth());

            villager.setCustomName(ChatColor.YELLOW + event.getPlayer().getName());
            villager.setCustomNameVisible(true);

            villager.setFallDistance(event.getPlayer().getFallDistance());
            villager.setRemoveWhenFarAway(false);
            villager.setVelocity(event.getPlayer().getVelocity());

            combatLoggers.add(villager);

            new BukkitRunnable() {

                public void run() {
                    if (!villager.isDead() && villager.isValid()) {
                        combatLoggers.remove(villager);
                        villager.remove();
                    }
                }

            }.runTaskLater(HCF.getInstance(), 30 * 20L);

            if (villager.getWorld().getEnvironment() == World.Environment.THE_END) {
                // check every second if the villager fell out of the world and kill the player if that happened.
                new BukkitRunnable() {

                    int tries = 0;

                    @Override
                    public void run() {
                        if (villager.getLocation().getBlockY() >= 0) {
                            tries++;

                            if (tries == 30) {
                                cancel();
                            }
                            return;
                        }

                        HCF.getInstance().getLogger().info(metadata.playerName + "'s combat logger at (" + villager.getLocation().getBlockX() + ", " + villager.getLocation().getBlockY() + ", " + villager.getLocation().getBlockZ() + ") died.");

                        // Deathban the player
                        HCF.getInstance().getDeathbanMap().deathban(metadata.playerUUID, metadata.deathBanTime);
                        Team team = HCF.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

                        // Take away DTR.
                        if (team != null) {
                            team.playerDeath(metadata.playerName, HCF.getInstance().getServerHandler().getDTRLoss(villager.getLocation()), null);
                        }

                        // store the death amount -- we'll use this later on.
                        int victimKills;

                        victimKills = HCF.getInstance().getMapHandler().getStatsHandler().getStats(metadata.playerUUID).getKills();

                        String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.DARK_RED + "[" + victimKills + "]" +  ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (HCF.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                                player.sendMessage(deathMessage);
                            } else {
                                if (team != null && team == HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId())) {
                                    player.sendMessage(deathMessage);
                                }
                            }
                        }

                        Player target = HCF.getInstance().getServer().getPlayer(metadata.playerUUID);

                        if (target == null) {
                            // Create an entity to load the player data
                            MinecraftServer server = ((CraftServer) HCF.getInstance().getServer()).getServer();
                            EntityPlayer entity = new EntityPlayer(server, server.E().getMinecraftWorld(), new GameProfile(metadata.playerUUID, metadata.playerName), Objects.requireNonNull(server.E().q_()).playerInteractManager);
                            target = entity.getBukkitEntity();

                            if (target != null) {
                                target.loadData();
                            }
                        }

                        if (target != null) {
                            EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();

                            target.getInventory().clear();
                            target.getInventory().setArmorContents(null);
                            humanTarget.getBukkitEntity().setHealth(0);

                            spoofWebsiteData(target, villager.getKiller());
                            target.saveData();
                        }

                        LastInvCommand.recordInventory(metadata.playerUUID, metadata.contents, metadata.armor);

                        cancel();
                        villager.remove();
                    }

                }.runTaskTimer(HCF.getInstance(), 0L, 20L);
            }
        }
    }

    public double calculateCombatLoggerHealth(Player player) {
        int potions = 0;
        boolean gapple = false;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() == Material.POTION && itemStack.getDurability() == (short) 16421) {
                potions++;
            } else if (!gapple && itemStack.getType() == Material.GOLDEN_APPLE && itemStack.getDurability() == (short) 1) {
                // Only let the player have one gapple count.
                potions += 15;
                gapple = true;
            }
        }

        return ((potions * 3.5D) + player.getHealth());
    }

    public static class CombatLoggerMetadata {

        private ItemStack[] contents;
        private ItemStack[] armor;
        private String playerName;
        private UUID playerUUID;
        private long deathBanTime;

    }

    private void spoofWebsiteData(Player killed, Player killer) {
        final BasicDBObject playerDeath = new BasicDBObject();

        if (killer != null) {
            playerDeath.append("healthLeft", (int) killer.getHealth());
            playerDeath.append("killerUUID", killer.getUniqueId().toString().replace("-", ""));
            /*
            playerDeath.append("killerInventory", PlayerInventorySerializer.getInsertableObject(killer));
             */
        } else {
            try{
                playerDeath.append("reason", "combat-logger");
            } catch (NullPointerException ignored) {}
        }

       // playerDeath.append("playerInventory", PlayerInventorySerializer.getInsertableObject(killed));
        playerDeath.append("uuid", killed.getUniqueId().toString().replace("-", ""));
        playerDeath.append("player", killed.getName());
        playerDeath.append("when", new Date());
        playerDeath.put("_id", UUID.randomUUID().toString().replaceAll("-", ""));

        new BukkitRunnable() {

            public void run() {
                HCF.getInstance().getMongoPool().getDB(HCF.MONGO_DB_NAME).getCollection("Deaths").insert(playerDeath);
            }

        }.runTaskAsynchronously(HCF.getInstance());
    }

}
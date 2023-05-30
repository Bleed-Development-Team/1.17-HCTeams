package net.frozenorb.foxtrot.deathmessage.listeners;

import com.google.common.collect.Maps;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.event.PlayerKilledEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.deathmessage.util.UnknownDamage;
import net.frozenorb.foxtrot.map.killstreaks.Killstreak;
import net.frozenorb.foxtrot.map.killstreaks.PersistentKillstreak;
import net.frozenorb.foxtrot.map.stats.StatsEntry;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Players;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DamageListener implements Listener {
    
    // kit-map only
    private Map<UUID, UUID> lastKilled = Maps.newHashMap();
    private Map<UUID, Integer> boosting = Maps.newHashMap();
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            CustomPlayerDamageEvent customEvent = new CustomPlayerDamageEvent(event, new UnknownDamage(player.getName(), event.getDamage()));
            
            HCF.getInstance().getServer().getPluginManager().callEvent(customEvent);
            DeathMessageHandler.addDamage(player, customEvent.getTrackerDamage());
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        DeathMessageHandler.clearDamage(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<Damage> record = DeathMessageHandler.getDamage(event.getEntity());
        
        event.setDeathMessage(null);
        
        String deathMessage;
        
        if (record != null) {
            Damage deathCause = record.get(record.size() - 1);
            
            // Hacky NMS to change the player's killer
            // System.out.println("The milliseconds since death is: " +
            // deathCause.getTimeDifference() + " this should be less than " +
            // TimeUnit.MINUTES.toMillis(1) );
            if (deathCause instanceof PlayerDamage && deathCause.getTimeDifference() < TimeUnit.MINUTES.toMillis(1)) {
                // System.out.println("Its a playerdamage thing");
                String killerName = ((PlayerDamage) deathCause).getDamager();
                Player killer = HCF.getInstance().getServer().getPlayerExact(killerName);
                
                if (killer != null && !HCF.getInstance().getInDuelPredicate().test(event.getEntity())) {

                    // kit-map death handling
                    if (HCF.getInstance().getMapHandler().isKitMap() || HCF.getInstance().getServerHandler().isVeltKitMap()) {
                        Player victim = event.getEntity();

                        // Call event
                        PlayerKilledEvent killedEvent = new PlayerKilledEvent(killer, victim);
                        HCF.getInstance().getServer().getPluginManager().callEvent(killedEvent);

                        // Prevent kill boosting
                        // Check if the victim's UUID is the same as the killer's last victim UUID
                        // Check if the victim's IP matches the killer's IP
                        if (lastKilled.containsKey(killer.getUniqueId()) && lastKilled.get(killer.getUniqueId()) == victim.getUniqueId()) {
                            boosting.putIfAbsent(killer.getUniqueId(), 0);
                            boosting.put(killer.getUniqueId(), boosting.get(killer.getUniqueId()) + 1);
                        } else {
                            boosting.put(killer.getUniqueId(), 0);
                        }

                        if (killer.equals(victim) || Players.isNaked(victim)) {
                            StatsEntry victimStats = HCF.getInstance().getMapHandler().getStatsHandler().getStats(victim);

                            victimStats.addDeath();
                        } else if (killer.getAddress().getAddress().getHostAddress().equalsIgnoreCase(victim.getAddress().getAddress().getHostAddress())) {
                            killer.sendMessage(ChatColor.RED + "Boost Check: You've killed a player on the same IP address as you.");
                        } else if (boosting.containsKey(killer.getUniqueId()) && boosting.get(killer.getUniqueId()) > 1) {
                            killer.sendMessage(ChatColor.RED + "Boost Check: You've killed " + victim.getName() + " " + boosting.get(killer.getUniqueId()) + " times.");
                            
                            StatsEntry victimStats = HCF.getInstance().getMapHandler().getStatsHandler().getStats(victim);
                            
                            victimStats.addDeath();
                        } else {
                            StatsEntry victimStats = HCF.getInstance().getMapHandler().getStatsHandler().getStats(victim);
                            StatsEntry killerStats = HCF.getInstance().getMapHandler().getStatsHandler().getStats(killer);
                            
                            victimStats.addDeath();
                            killerStats.addKill();

                            
                            lastKilled.put(killer.getUniqueId(), victim.getUniqueId());
                            
                            Killstreak killstreak = HCF.getInstance().getMapHandler().getKillstreakHandler().check(killerStats.getKillstreak());
                            
                            if (killstreak != null) {
                                killstreak.apply(killer);
                                
                                Bukkit.broadcastMessage(killer.getDisplayName() + ChatColor.YELLOW + " has gotten the " + ChatColor.RED + killstreak.getName() + ChatColor.YELLOW + " killstreak!");

                                List<PersistentKillstreak> persistent = HCF.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks(killer, killerStats.getKillstreak());

                                for (PersistentKillstreak persistentStreak : persistent) {
                                    if (persistentStreak.matchesExactly(killerStats.getKillstreak())) {
                                        Bukkit.broadcastMessage(killer.getDisplayName() + ChatColor.YELLOW + " has gotten the " + ChatColor.RED + killstreak.getName() + ChatColor.YELLOW + " killstreak!");
                                    }

                                    persistentStreak.apply(killer);
                                }
                            }
                            
                            HCF.getInstance().getKillsMap().setKills(killer.getUniqueId(), killerStats.getKills());
                            HCF.getInstance().getDeathsMap().setDeaths(victim.getUniqueId(), victimStats.getDeaths());
                        }
                        StatsEntry victimStats = HCF.getInstance().getMapHandler().getStatsHandler().getStats(victim);
                        StatsEntry killerStats = HCF.getInstance().getMapHandler().getStatsHandler().getStats(killer);

                        victimStats.addDeath();
                        killerStats.addKill();

                        if (HCF.getInstance().getServerHandler().isHardcore()) {
                            event.getDrops().add(HCF.getInstance().getServerHandler().generateDeathSign(event.getEntity().getName(), killer.getName()));
                        }
                    }
                }
            }
            
            deathMessage = deathCause.getDeathMessage();
        } else {
            deathMessage = new UnknownDamage(event.getEntity().getName(), 1).getDeathMessage();
        }
        
        Player killer = event.getEntity().getKiller();
        Player victim = event.getEntity().getPlayer();
        
        Team killerTeam = killer == null ? null : HCF.getInstance().getTeamHandler().getTeam(killer);
        Team deadTeam = HCF.getInstance().getTeamHandler().getTeam(event.getEntity());
        StatsEntry killerStats = killer == null ? null : HCF.getInstance().getMapHandler().getStatsHandler().getStats(killer);

        if (killer != null){
            if (!isNaked(victim.getInventory())) {
                killerStats.addKill();
            }
        }

        if (killerTeam != null) {
            if (!isNaked(victim.getInventory())) {
                killerTeam.setKills(killerTeam.getKills() + 1);
            }
        }
        
        if (deadTeam != null) {
            deadTeam.setDeaths(deadTeam.getDeaths() + 1);
        }

        if (killer != null){
            HCF.getInstance().getGemsMap().addGems(killer.getUniqueId(), 5);
            killer.sendMessage(CC.translate("&aYou earned &2+5 &agems!"));
        }

        Bukkit.getScheduler().scheduleAsyncDelayedTask(HCF.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {


                if (HCF.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                    player.sendMessage(deathMessage);
                } else {
                    if (HCF.getInstance().getTeamHandler().getTeam(player) == null) {
                        continue;
                    }
                    
                    // send them the message if the player who died was on their team
                    if (HCF.getInstance().getTeamHandler().getTeam(event.getEntity()) != null && HCF.getInstance().getTeamHandler().getTeam(player).equals(HCF.getInstance().getTeamHandler().getTeam(event.getEntity()))) {
                        player.sendMessage(deathMessage);
                    }
                    
                    // send them the message if the killer was on their team
                    if (HCF.getInstance().getTeamHandler().getTeam(killer) != null && HCF.getInstance().getTeamHandler().getTeam(player).equals(HCF.getInstance().getTeamHandler().getTeam(killer))) {
                        player.sendMessage(deathMessage);
                    }
                }
            }

            if (killer != null){
                TextComponent component = new TextComponent(CC.translate("&4✗ &eYou have killed &6&l" + event.getEntity().getName() + " &4✗"));
                killer.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);

                killer.playSound(killer.getLocation(), Sound.ENTITY_IRON_GOLEM_DEATH, 1.0f, 1.0f);
            }


        });
        
        //DeathTracker.logDeath(event.getEntity(), event.getEntity().getKiller());
        DeathMessageHandler.clearDamage(event.getEntity());
        HCF.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity()).addDeath();
        HCF.getInstance().getDeathsMap().setDeaths(event.getEntity().getUniqueId(), HCF.getInstance().getDeathsMap().getDeaths(event.getEntity().getUniqueId()) + 1);
    }
    
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (HCF.getInstance().getMapHandler().isKitMap() || HCF.getInstance().getServerHandler().isVeltKitMap()) {
            checkKillstreaks(event.getPlayer());
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (HCF.getInstance().getMapHandler().isKitMap() || HCF.getInstance().getServerHandler().isVeltKitMap()) {
            checkKillstreaks(event.getPlayer());
        }
    }
    
    private void checkKillstreaks(Player player) {
        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            int killstreak = HCF.getInstance().getMapHandler().getStatsHandler().getStats(player).getKillstreak();
            List<PersistentKillstreak> persistent = HCF.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks(player, killstreak);
            
            for (PersistentKillstreak persistentStreak : persistent) {
                persistentStreak.apply(player);
            }
        }, 5L);
    }


    public boolean isNaked(PlayerInventory armor) {
        return (armor.getHelmet() == null &&
                armor.getChestplate() == null &&
                armor.getLeggings() == null &&
                armor.getBoots() == null);
    }
}

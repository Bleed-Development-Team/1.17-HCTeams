package net.frozenorb.foxtrot.server.listener;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.partnerpackages.PartnerPackageHandler;
import net.frozenorb.foxtrot.gameplay.events.region.carepackage.CarePackageHandler;
import net.frozenorb.foxtrot.server.listener.impl.*;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.team.upgrades.UpgradeListener;

public class ListenerHandler {
    
    public ListenerHandler(HCF plugin){
        plugin.getServer().getPluginManager().registerEvents(new MapListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new AntiGlitchListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new BasicPreventionListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new BorderListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents((HCF.getInstance().combatLoggerListener = new CombatLoggerListener()), plugin);
        plugin.getServer().getPluginManager().registerEvents(new CrowbarListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new DeathbanListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new EnchantmentLimiterListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new EnderpearlCooldownHandler(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new EndListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new FoundDiamondsListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new FoxListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new GoldenAppleListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new KOTHRewardKeyListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PartnerPackageHandler(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PvPTimerListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PotionLimiterListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new NetherPortalListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PortalTrapListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new SignSubclaimListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new SpawnerTrackerListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new SpawnListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new SpawnTagListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new StaffUtilsListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new TeamListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new UpgradeListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new WebsiteListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new StatTrakListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new TitleListener(), plugin);

        if (plugin.getServerHandler().isReduceArmorDamage()) {
            plugin.getServer().getPluginManager().registerEvents(new ArmorDamageListener(), plugin);
        }

        if (plugin.getServerHandler().isBlockEntitiesThroughPortals()) {
            plugin.getServer().getPluginManager().registerEvents(new EntityPortalListener(), plugin);
        }

        if (plugin.getServerHandler().isBlockRemovalEnabled()) {
            plugin.getServer().getPluginManager().registerEvents(new BlockRegenListener(), plugin);
        }

        // Register kitmap specific listeners
        if (plugin.getServerHandler().isVeltKitMap() || plugin.getMapHandler().isKitMap()) {
            plugin.getServer().getPluginManager().registerEvents(new KitMapListener(), plugin);
            plugin.getServer().getPluginManager().registerEvents(new CarePackageHandler(), plugin);
        }

        plugin.getServer().getPluginManager().registerEvents(new BlockConvenienceListener(), plugin);

    }
    
}

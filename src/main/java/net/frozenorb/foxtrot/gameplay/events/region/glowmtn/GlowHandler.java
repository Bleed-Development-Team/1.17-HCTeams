package net.frozenorb.foxtrot.gameplay.events.region.glowmtn;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.listeners.GlowListener;
import net.frozenorb.foxtrot.team.claims.Claim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class GlowHandler {

    private static File file;
    @Getter private final static String glowTeamName = "Glowstone";
    @Getter @Setter private GlowMountain glowMountain;

    public GlowHandler() {
        try {
            file = new File(HCF.getInstance().getDataFolder(), "glowmtn.json");

            if (!file.exists()) {
                glowMountain = null;

                if (file.createNewFile()) {
                    HCF.getInstance().getLogger().warning("Created a new glow mountain json file.");
                }
            } else {
                glowMountain = HCF.GSON.fromJson(FileUtils.readFileToString(file), GlowMountain.class);
                HCF.getInstance().getLogger().info("Successfully loaded the glow mountain from file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int secs = HCF.getInstance().getServerHandler().isHardcore() ? (90 * 60 * 20) : HCF.getInstance().getServerHandler().getTabServerName().contains("cane") ? HCF.getInstance().getMapHandler().getTeamSize() == 8 ? 20 * 25 * 60 : 20 * 45 * 60 : 12000;
        HCF.getInstance().getServer().getScheduler().runTaskTimer(HCF.getInstance(), () -> {
            getGlowMountain().reset();

            // Broadcast the reset
            Bukkit.broadcastMessage(ChatColor.GOLD + "[Glowstone Mountain]" + ChatColor.GREEN + " All glowstone has been reset!");
        }, secs, secs);

        HCF.getInstance().getServer().getPluginManager().registerEvents(new GlowListener(), HCF.getInstance());
    }

    public void save() {
        try {
            FileUtils.write(file, HCF.GSON.toJson(glowMountain));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasGlowMountain() {
        return glowMountain != null;
    }

    public static Claim getClaim() {
        return HCF.getInstance().getTeamHandler().getTeam(glowTeamName).getClaims().get(0); // null if no glowmtn is set!
    }
}
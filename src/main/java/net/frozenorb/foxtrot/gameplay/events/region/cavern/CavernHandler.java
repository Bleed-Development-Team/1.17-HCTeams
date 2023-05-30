package net.frozenorb.foxtrot.gameplay.events.region.cavern;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.listeners.CavernListener;
import net.frozenorb.foxtrot.team.claims.Claim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CavernHandler {

    private static File file;
    @Getter private final static String cavernTeamName = "Cavern";
    @Getter @Setter private Cavern cavern;

    public CavernHandler() {
        try {
            file = new File(HCF.getInstance().getDataFolder(), "cavern.json");

            if (!file.exists()) {
                cavern = null;

                if (file.createNewFile()) {
                    HCF.getInstance().getLogger().warning("Created a new Cavern json file.");
                }
            } else {
                cavern = HCF.GSON.fromJson(FileUtils.readFileToString(file), Cavern.class);
                HCF.getInstance().getLogger().info("Successfully loaded the Cavern from file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HCF.getInstance().getServer().getScheduler().runTaskTimer(HCF.getInstance(), () -> {
            if (getCavern() == null || HCF.getInstance().getTeamHandler().getTeam(cavernTeamName) == null) return;
            getCavern().reset();
            // Broadcast the reset
            Bukkit.broadcastMessage(ChatColor.AQUA + "[Cavern]" + ChatColor.GREEN + " All ores have been reset!");
        }, 20 * 60 * 60, 20 * 60 * 60);

        HCF.getInstance().getServer().getPluginManager().registerEvents(new CavernListener(), HCF.getInstance());
    }

    public void save() {
        try {
            FileUtils.write(file, HCF.GSON.toJson(cavern));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasCavern() {
        return cavern != null;
    }

    public static Claim getClaim() {
        return HCF.getInstance().getTeamHandler().getTeam(cavernTeamName).getClaims().get(0); // null if no glowmtn is set!
    }
}
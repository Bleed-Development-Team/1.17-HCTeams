package net.frozenorb.foxtrot.util;

import net.frozenorb.foxtrot.HCF;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class RegenUtils {

    private static final Set<Location> allEntries = new HashSet<>();

    public static void schedule(Block block, int amount, TimeUnit unit, Callback<Block> onRegen, Predicate<Block> willRegen) {
        int seconds = (int) unit.toSeconds(amount);

        allEntries.add(block.getLocation());

        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            if (willRegen.test(block)) {
                onRegen.callback(block);

                block.setType(Material.AIR);
                allEntries.remove(block.getLocation());
            }
        }, seconds * 20L);
    }

    public static void resetAll() {
        System.out.println("Resetting blocks");
        long start = System.currentTimeMillis();

        for (Location location : allEntries) {
            location.getBlock().setType(Material.AIR);
        }

        System.out.println("Reset blocks in " + (System.currentTimeMillis() - start) + "ms.");
    }

}

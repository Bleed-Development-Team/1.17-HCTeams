package net.frozenorb.foxtrot.gameplay.archerupgrades.type;

import net.frozenorb.foxtrot.gameplay.archerupgrades.ArcherUpgrade;
import org.bukkit.DyeColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Phantom extends ArcherUpgrade {
    @Override
    public String getName() {
        return "&e&lPhantom";
    }

    @Override
    public PotionEffect getEffect() {
        return new PotionEffect(PotionEffectType.BLINDNESS, 20 * 4, 1);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }

    @Override
    public String getColor() {
        return "&e";
    }

    @Override
    public int getCost() {
        return 100;
    }
}

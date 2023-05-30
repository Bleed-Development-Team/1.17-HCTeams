package net.frozenorb.foxtrot.gameplay.archerupgrades.type;

import net.frozenorb.foxtrot.gameplay.archerupgrades.ArcherUpgrade;
import org.bukkit.DyeColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Venom extends ArcherUpgrade {
    @Override
    public String getName() {
        return "&5&lVenom";
    }

    @Override
    public PotionEffect getEffect() {
        return new PotionEffect(PotionEffectType.WITHER, 20 * 4, 1);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }

    @Override
    public String getColor() {
        return "&5";
    }

    @Override
    public int getCost() {
        return 125;
    }
}

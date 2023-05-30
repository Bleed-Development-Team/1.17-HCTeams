package net.frozenorb.foxtrot.gameplay.archerupgrades.type;

import net.frozenorb.foxtrot.gameplay.archerupgrades.ArcherUpgrade;
import org.bukkit.DyeColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Fainter extends ArcherUpgrade {
    @Override
    public String getName() {
        return "&9&lFainter";
    }

    @Override
    public PotionEffect getEffect() {
        return new PotionEffect(PotionEffectType.SLOW, 20 * 4, 1);

    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }

    @Override
    public String getColor() {
        return "&9";
    }

    @Override
    public int getCost() {
        return 75;
    }
}

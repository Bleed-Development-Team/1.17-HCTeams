package net.frozenorb.foxtrot.gameplay.archerupgrades.type;

import net.frozenorb.foxtrot.gameplay.archerupgrades.ArcherUpgrade;
import org.bukkit.DyeColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Medusa extends ArcherUpgrade {
    @Override
    public String getName() {
        return "&2&lMedusa";
    }

    @Override
    public PotionEffect getEffect() {
        return new PotionEffect(PotionEffectType.POISON, 20 * 4, 1);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }

    @Override
    public String getColor() {
        return "&2";
    }

    @Override
    public int getCost() {
        return 75;
    }
}

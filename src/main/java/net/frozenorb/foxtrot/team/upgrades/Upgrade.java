package net.frozenorb.foxtrot.team.upgrades;

import lombok.Getter;
import org.bukkit.Effect;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Upgrade {

    STRENGTH(new ArrayList<>(Collections.singletonList(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0))), 400),
    RESISTANCE(new ArrayList<>(Collections.singletonList(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0))), 150);

    @Getter private List<PotionEffect> effects;
    @Getter private int cost;

    Upgrade(List<PotionEffect> effects, int cost){
        this.effects = effects;
        this.cost = cost;
    }
}

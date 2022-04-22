package net.frozenorb.foxtrot.extras.ability;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.impl.*;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityHandler {

    @Getter private final Map<String, Ability> abilities = new HashMap<>();

    public AbilityHandler(){
        registerTimers();

        abilities.put("bone", new BoneAbility());
        abilities.put("powerstone", new PowerstoneAbility());
        abilities.put("combo", new ComboAbility());
        abilities.put("switcher", new SwitcherAbility());
        abilities.put("potioncounter", new PotionCounterAbility());

        for (Ability ability : abilities.values()){
            Bukkit.getServer().getPluginManager().registerEvents(ability, Foxtrot.getInstance());
        }
    }

    private void registerTimers(){
        Cooldown.createCooldown("partner");

        Cooldown.createCooldown("bone");
        Cooldown.createCooldown("bone-eff");

        Cooldown.createCooldown("powerstone");

        Cooldown.createCooldown("combo");
        Cooldown.createCooldown("combo-eff");
        Cooldown.createCooldown("potioncounter");
        Cooldown.createCooldown("switcher");
    }

    public List<Ability> getAbilities(){
        return new ArrayList<>(abilities.values());
    }

}

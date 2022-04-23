package net.frozenorb.foxtrot.extras.ability;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.ability.impl.*;
import net.frozenorb.foxtrot.extras.ability.impl.portable.*;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityHandler implements Listener {

    private final Map<String, Ability> abilities = new HashMap<>();

    public AbilityHandler(){
        registerTimers();

        abilities.put("bone", new BoneAbility());
        abilities.put("powerstone", new PowerstoneAbility());
        abilities.put("combo", new ComboAbility());
        abilities.put("switcher", new SwitcherAbility());
        abilities.put("potioncounter", new PotionCounterAbility());
        abilities.put("antipearl", new AntiPearlAbility());
        abilities.put("medkit", new MedkitAbility());
        abilities.put("backtotheroots", new BackToTheRootsAbility());
        abilities.put("rocket", new RocketAbility());
        abilities.put("lucky-mode", new LuckyModeAbility());
        abilities.put("strength", new StrengthTwoAbility());
        abilities.put("resistance", new ResistanceTwoAbility());
        abilities.put("regen", new RegenerationAbility());
        abilities.put("invis", new InvisibilityAbility());
        abilities.put("jump", new JumpAbility());

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

        Cooldown.createCooldown("medkit");

        Cooldown.createCooldown("switcher");
        Cooldown.createCooldown("antipearl");

        Cooldown.createCooldown("rocket");
        Cooldown.createCooldown("backtotheroots");

        Cooldown.createCooldown("lucky");
        Cooldown.createCooldown("portable");
    }

    public List<Ability> getAbilities(){
        return new ArrayList<>(abilities.values());
    }


}

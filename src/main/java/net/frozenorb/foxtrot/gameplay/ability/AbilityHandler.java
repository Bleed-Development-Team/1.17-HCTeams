package net.frozenorb.foxtrot.gameplay.ability;

import lombok.Getter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.ability.damage.DamageAbilityListener;
import net.frozenorb.foxtrot.gameplay.ability.damage.type.*;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbilityListener;
import net.frozenorb.foxtrot.gameplay.ability.interact.type.portable.PortableBard;
import net.frozenorb.foxtrot.gameplay.ability.interact.type.portable.PortableBardEffect;
import net.frozenorb.foxtrot.gameplay.ability.damage.DamageAbility;
import net.frozenorb.foxtrot.gameplay.ability.interact.InteractAbility;
import net.frozenorb.foxtrot.gameplay.ability.interact.type.*;
import net.frozenorb.foxtrot.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AbilityHandler {

    @Getter private Map<String, InteractAbility> interactAbilities = new HashMap<>();
    @Getter private Map<String, DamageAbility> damageAbilities = new HashMap<>();

    @Getter private Map<String, Ability> special = new HashMap<>();

    @Getter private ArrayList<Ability> allAbilities = new ArrayList<>();

    public AbilityHandler(){
        // Interact
        //interactAbilities.put("guardian-angel", new GuardianAngel());
        interactAbilities.put("powerstone", new Powerstone());
        interactAbilities.put("medkit", new Medkit());
        interactAbilities.put("combo", new Combo());
        interactAbilities.put("rocket", new Rocket());
        interactAbilities.put("ballofrage", new BallOfRage());
        interactAbilities.put("switcher", new Switcher());
        interactAbilities.put("timewarp", new TimeWarp());
        interactAbilities.put("timestone", new TimeStone());
        interactAbilities.put("ninjastar", new NinjaStar());
        interactAbilities.put("secondchance", new SecondChance());
        //interactAbilities.put("antitrapper", new AntiTrapper());

        // Portable

        interactAbilities.put("strength", new PortableBardEffect("strength", "&c&lStrength", 90, Material.BLAZE_POWDER, "&c", new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 1)));
        interactAbilities.put("resistance", new PortableBardEffect("resistance", "&6&lResistance", 90, Material.IRON_INGOT, "&6", new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 2)));
        interactAbilities.put("regeneration", new PortableBardEffect("regeneration", "&d&lRegeneration", 60, Material.GHAST_TEAR, "&d", new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 2)));
        interactAbilities.put("jump", new PortableBardEffect("jump", "&a&lJump", 90, Material.FEATHER, "&a", new PotionEffect(PotionEffectType.JUMP, 20 * 6, 6)));
        interactAbilities.put("speed", new PortableBardEffect("speed", "&b&lSpeed", 90, Material.SUGAR, "&b", new PotionEffect(PotionEffectType.SPEED, 20 * 6, 2)));
        interactAbilities.put("portablebard", new PortableBard());

        // Damage
        damageAbilities.put("focus-mode", new FocusMode());
        damageAbilities.put("antibuild", new AntiBuild());
        damageAbilities.put("antipearl", new AntiPearl());
        damageAbilities.put("freezer", new Freezer());
        damageAbilities.put("effectstealer", new EffectStealer());
        damageAbilities.put("scrambler", new Scrambler());

        for (String key : damageAbilities.keySet()){
            Cooldown.createCooldown(key);
        }

        for (String key : interactAbilities.keySet()){
            Cooldown.createCooldown(key);
        }

        allAbilities.addAll(interactAbilities.values());
        allAbilities.addAll(damageAbilities.values());

        Bukkit.getPluginManager().registerEvents(new DamageAbilityListener(), HCF.getInstance());
        Bukkit.getPluginManager().registerEvents(new InteractAbilityListener(), HCF.getInstance());
    }

    public InteractAbility getInteractAbility(ItemStack itemStack){
        InteractAbility ability = null;
        for (InteractAbility item : this.interactAbilities.values()){
            if (item.getItemStack().isSimilar(itemStack)){
                ability = item;
                break;
            }
        }

        return ability;
    }

    public DamageAbility getDamageAbility(ItemStack itemStack){
        DamageAbility ability = null;
        for (DamageAbility item : this.damageAbilities.values()){
            if (item.getItemStack().isSimilar(itemStack)){
                ability = item;
                break;
            }
        }

        return ability;
    }



}

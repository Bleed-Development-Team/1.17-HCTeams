package net.frozenorb.foxtrot.gameplay.ability.recipe;

import net.frozenorb.foxtrot.HCF;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class AbilityRecipeHandler {

    public AbilityRecipeHandler(){
        //ShapedRecipe bone = addRecipe(Items.getBoneAbility(), "bone_recipe", "ADA", "ASA", "ASA");
        /*
        bone.setIngredient('A', Material.AIR);
        bone.setIngredient('D', Material.DIAMOND);
        bone.setIngredient('B', Material.BONE);

         */

        System.out.println("Adding recipes");
    }

    private ShapedRecipe addRecipe(ItemStack result, String key, String... shape){
        ItemStack resultItem = new ItemStack(result);
        NamespacedKey recipeKey = new NamespacedKey(HCF.getInstance(), key);

        ShapedRecipe shapedRecipe = new ShapedRecipe(recipeKey, resultItem);
        shapedRecipe.shape(shape);

        return shapedRecipe;
    }

    private void registerRecipe(ShapedRecipe recipe){
        HCF.getInstance().getServer().addRecipe(recipe);
    }

}

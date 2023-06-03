package net.frozenorb.foxtrot.gameplay.nms;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.nms.pots.CustomPotion;
import net.minecraft.server.v1_16_R3.CreativeModeTab;
import net.minecraft.server.v1_16_R3.Item;
import net.minecraft.server.v1_16_R3.ItemSplashPotion;
import net.minecraft.server.v1_16_R3.Items;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * TODO: Write class description here
 *
 * @author Nopox
 * @project 1.17-HCTeams
 * @since 6/2/2023
 */
public class HCFItemRegistry {

    private static Unsafe unsafe;

    static {
        try{
            final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void register() {
        HCF.getInstance().getLogger().info("This method is being called.");
        try {
            HCF.getInstance().getLogger().info("Setting up the custom ITEMS <!~W2dsaddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
            Field splashPotionField = Items.class.getDeclaredField("SPLASH_POTION");
            splashPotionField.setAccessible(true);
            HCF.getInstance().getLogger().info("Passed first thing");

            Object fieldBase = unsafe.staticFieldBase(splashPotionField);
            long fieldOffset = unsafe.staticFieldOffset(splashPotionField);
            HCF.getInstance().getLogger().info("Passed second thing");

            Method aMethod = Items.class.getDeclaredMethod("a", String.class, Item.class);
            aMethod.setAccessible(true);
            HCF.getInstance().getLogger().info("Passed third thing");
reset
            unsafe.putObject(fieldBase, fieldOffset, aMethod.invoke(null, "splash_potion", new CustomPotion((new Item.Info()).a(1).a(CreativeModeTab.k))));
            HCF.getInstance().getLogger().info("Passed foutrth thing");
            splashPotionField.setAccessible(false);
            aMethod.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

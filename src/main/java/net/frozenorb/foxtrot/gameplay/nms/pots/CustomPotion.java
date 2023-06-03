package net.frozenorb.foxtrot.gameplay.nms.pots;

import net.frozenorb.foxtrot.HCF;
import net.minecraft.server.v1_16_R3.*;

/**
 * TODO: Write class description here
 *
 * @author Nopox
 * @project 1.17-HCTeams
 * @since 6/2/2023
 */
public class CustomPotion extends ItemSplashPotion {
    public CustomPotion(Info var0) {
        super(var0);
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World var0, EntityHuman var1, EnumHand var2) {
        var1.getBukkitEntity().sendMessage("IM GONNA BUST");
        HCF.getInstance().getLogger().info("CUSTOM POT was thrown");
        ItemStack var3 = var1.b(var2);
        if (!var0.isClientSide) {
            EntityPotion var4 = new CustomPotionEntity(var0, var1);
            var4.setItem(var3);
            var4.a(var1, var1.pitch, var1.yaw, -20.0F, 0.5F, 1.0F);
            var0.addEntity(var4);
        }

        var1.b(StatisticList.ITEM_USED.b(this));
        if (!var1.abilities.canInstantlyBuild) {
            var3.subtract(1);
        }

        return InteractionResultWrapper.a(var3, var0.s_());
    }
}

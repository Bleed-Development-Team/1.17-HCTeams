package net.frozenorb.foxtrot.gameplay.nms.pots;

import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityPotion;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;

/**
 * This class represnts a
 *
 * @author Nopox
 * @project 1.17-HCTeams
 * @since 6/2/2023
 */
public class CustomPotionEntity extends EntityPotion {


    public CustomPotionEntity(World world, EntityLiving entityliving) {
        super(world, entityliving);
    }

    @Override
    protected float k() {
        return (float) (0.05 * 2.0);
    }
}

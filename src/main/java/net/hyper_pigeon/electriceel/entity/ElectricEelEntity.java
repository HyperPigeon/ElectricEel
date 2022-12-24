package net.hyper_pigeon.electriceel.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.AquaticLookControl;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.goal.MoveIntoWaterGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.AmphibiousNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.world.World;
import org.quiltmc.qsl.entity.multipart.api.EntityPart;
import org.quiltmc.qsl.entity.multipart.api.MultipartEntity;

public class ElectricEelEntity extends WaterCreatureEntity {

    public ElectricEelEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.navigation = new AmphibiousNavigation(this, world);
        this.moveControl = new AquaticMoveControl(this, 85, 90, 0.1f, 0.1f, true);
        this.lookControl = new AquaticLookControl(this, 10);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new MoveIntoWaterGoal(this));
        this.goalSelector.add(1, new SwimAroundGoal(this, 1.0, 10));
    }




    public static DefaultAttributeContainer.Builder createElectricEelAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.2).add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0);
    }

}

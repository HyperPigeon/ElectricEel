package net.hyper_pigeon.electriceel.entity;

import net.hyper_pigeon.electriceel.entity.ai.control.EelLookControl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.goal.MoveIntoWaterGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.AmphibiousNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.world.World;
import org.quiltmc.qsl.entity.multipart.api.EntityPart;
import org.quiltmc.qsl.entity.multipart.api.MultipartEntity;

public class ElectricEelEntity extends WaterCreatureEntity implements MultipartEntity {

    public final ElectricEelPart[] bodySegments = new ElectricEelPart[10];

    public ElectricEelEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.navigation = new AmphibiousNavigation(this, world);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.1f, 0.1f, true);
        this.lookControl = new EelLookControl(this);

        for(int i = 0; i < 10; i++){
            bodySegments[i] = new ElectricEelPart(this,0.33F,0.33F, i);
            bodySegments[i].setInvisible(false);
            bodySegments[i].refreshPositionAndAngles(getX(),getY(),getZ()-i*0.15,getYaw(),getPitch());
        }

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

    public int getLookPitchSpeed() {
        return 1;
    }

    public int getBodyYawSpeed() {
        return 1;
    }

    public void tickMovement() {
        super.tickMovement();
        if (this.world.isClient) {
            for (int i = 0; i < 2; ++i) {
                this.world
                        .addParticle(
                                DustParticleEffect.DEFAULT,
                                this.getParticleX(0.5),
                                this.getRandomBodyY() - 0.25,
                                this.getParticleZ(3),
                                (this.random.nextDouble() - 0.5) * 2.0,
                                -this.random.nextDouble(),
                                (this.random.nextDouble() - 0.5) * 2.0
                        );
            }
        }

        for(int i = 0; i < 10; i++){
            Entity leader = i == 0 ? this : this.bodySegments[i - 1];
            ElectricEelPart electricEelPart = this.bodySegments[i];
            electricEelPart.movePart(leader);
        }

    }

    @Override
    public EntityPart<?>[] getEntityParts() {
        return bodySegments;
    }

    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        ElectricEelPart[] electricEelParts = bodySegments;
        for(int i = 0; i < electricEelParts.length; ++i) {
            electricEelParts[i].setId(i + packet.getId() + 1);
        }
    }

}

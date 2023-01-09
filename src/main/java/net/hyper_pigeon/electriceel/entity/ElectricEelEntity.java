package net.hyper_pigeon.electriceel.entity;

import net.hyper_pigeon.electriceel.ElectricEel;
import net.hyper_pigeon.electriceel.entity.ai.control.EelLookControl;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.goal.MoveIntoWaterGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.AmphibiousNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.quiltmc.qsl.entity.multipart.api.EntityPart;
import org.quiltmc.qsl.entity.multipart.api.MultipartEntity;

import java.util.List;

public class ElectricEelEntity extends WaterCreatureEntity implements MultipartEntity {

    public final ElectricEelPart[] bodySegments = new ElectricEelPart[10];

    private int charge = 3;


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
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.2).add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0);
    }

    public int getLookPitchSpeed() {
        return 1;
    }

    public int getLookYawSpeed(){
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
                                ParticleTypes.ELECTRIC_SPARK,
                                this.getParticleX(0.75),
                                this.getRandomBodyY() - 0.25,
                                this.getParticleZ(0.75),
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

    public boolean damage(DamageSource source, float amount) {

        if(source.getAttacker() != null){
            source.getAttacker().damage(DamageSource.LIGHTNING_BOLT, 3.0F);
            this.pulse(true, true);
            if(world.isClient()){
                world.playSound(this.getX(),
                        this.getY(),
                        this.getZ(),
                        SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
                        SoundCategory.NEUTRAL,
                        10.0F,
                        15F + this.random.nextFloat() * 0.5F,
                        false);
            }
        }

        return super.damage(source,amount);
    }

    protected void onBlockCollision(BlockState state){
        super.onBlockCollision(state);
        if(state.getBlock().equals(Blocks.LIGHTNING_ROD)){
            this.pulse(true, true);
        }
    }

    private void pulse(boolean damaging, boolean seizure_inducing) {
        Box pulseBox = this.getBoundingBox().expand(charge);
        List<Entity> collidedEntities = this.world.getOtherEntities(this, pulseBox, entity -> entity.isAlive() && (entity instanceof LivingEntity) && !entity.getType().equals(ElectricEel.ELECTRIC_EEL_ENTITY));

        for(Entity entity : collidedEntities){
            LivingEntity livingEntity = (LivingEntity) entity;
            if(damaging)
                entity.damage(DamageSource.LIGHTNING_BOLT,charge/3);
            if(seizure_inducing && !entity.isPlayer()) {
                livingEntity.addStatusEffect(new StatusEffectInstance(ElectricEel.SHOCK_STATUS_EFFECT,200));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,200, 4));
            }
        }

        for(BlockPos blockPos : BlockPos.iterate(
                MathHelper.floor(pulseBox.minX),
                MathHelper.floor(pulseBox.minY),
                MathHelper.floor(pulseBox.minZ),
                MathHelper.floor(pulseBox.maxX),
                MathHelper.floor(pulseBox.maxY),
                MathHelper.floor(pulseBox.maxZ)
        )) {
            BlockState blockState = this.world.getBlockState(blockPos);
            if(blockState.isOf(Blocks.LIGHTNING_ROD)){
                LightningRodBlock lightningRodBlock = (LightningRodBlock) blockState.getBlock();
                lightningRodBlock.setPowered(blockState, world,blockPos);
            }
        }

    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        charge = nbt.getInt("charge");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("charge",charge);
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

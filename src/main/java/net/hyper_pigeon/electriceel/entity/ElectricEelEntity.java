package net.hyper_pigeon.electriceel.entity;

import net.hyper_pigeon.electriceel.ElectricEel;
import net.hyper_pigeon.electriceel.entity.ai.goal.MoveToAndEatFishEntityGoal;
import net.hyper_pigeon.electriceel.entity.ai.goal.MoveToAndEatFishItemGoal;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.AquaticLookControl;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.AmphibiousNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.entity.multipart.api.EntityPart;
import org.quiltmc.qsl.entity.multipart.api.MultipartEntity;

import java.util.List;

public class ElectricEelEntity extends WaterCreatureEntity implements MultipartEntity, RangedAttackMob {


    public final ElectricEelPart[] bodySegments = new ElectricEelPart[10];

    private int charge = 3;
    private int hungerCooldown = 0;
    private int pulseCooldown = 60;
    private int beamCooldown = 10;

    public static final TrackedData<Boolean> FEEDING = DataTracker.registerData(ElectricEelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    public ElectricEelEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.navigation = new AmphibiousNavigation(this, world);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.1f, 0.5f, true);
        this.lookControl = new AquaticLookControl(this,20);

        for(int i = 0; i < 10; i++){
            bodySegments[i] = new ElectricEelPart(this,0.33F,0.33F, i);
            bodySegments[i].setInvisible(false);
            bodySegments[i].refreshPositionAndAngles(getX(),getY(),getZ()-i*0.15,getYaw(),getPitch());
        }

    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setAir(this.getMaxAir());
        this.setPitch(0.0F);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }


    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(0, new MoveIntoWaterGoal(this));
        this.goalSelector.add(0, new BreatheAirGoal(this));
        this.goalSelector.add(1, new MoveToAndEatFishItemGoal(this));
        this.goalSelector.add(1,new MoveToAndEatFishEntityGoal(this));
        this.goalSelector.add(2, new ElectricEelEntity.PulseAttackGoal(this, 1.5F, 60,8));
        this.goalSelector.add(3, new SwimAroundGoal(this, 1.0, 10));

        this.targetSelector.add(1, new TargetGoal<>(this, FishEntity.class, false,  livingEntity -> this.getHungerCooldown() <= 0));
    }


    public static DefaultAttributeContainer.Builder createElectricEelAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.1).add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FEEDING, false);
    }

    public boolean isFeeding(){
        return this.dataTracker.get(FEEDING);
    }

    public void setFeeding(boolean value){
        this.dataTracker.set(FEEDING,value);
    }


    public int getLookPitchSpeed(){
        return 1;
    }

    public int getBodyYawSpeed() {
        return 1;
    }

    public int getMaxAir() {
        return 24800;
    }

    protected int getNextAirOnLand(int air) {
        return this.getMaxAir();
    }

    public boolean canBreatheInWater() {
        return false;
    }

    protected void tickWaterBreathingAir(int air) {
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.3F;
    }

    public void setHungerCooldown(int value){
        this.hungerCooldown = value;
    }

    public int getHungerCooldown(){
        return hungerCooldown;
    }

    public boolean canPickUpLoot(){
        return isFeeding();
    }

    public boolean canPickupItem(ItemStack stack) {
        return stack.getItem().equals(Items.SALMON) || stack.getItem().equals(Items.COD) || stack.getItem().equals(Items.TROPICAL_FISH);
    }

    protected void loot(ItemEntity item) {

        if(item.getStack().getItem().equals(Items.SALMON)){
            this.charge = 3;
        }
        else if(item.getStack().getItem().equals(Items.COD)){
            this.charge = 6;
        }
        else if(item.getStack().getItem().equals(Items.TROPICAL_FISH)) {
            this.charge = 9;
        }

        ItemStack itemStack = item.getStack();
        itemStack.decrement(1);
        this.heal(5.0F);
        if (itemStack.isEmpty()) {
            item.discard();
        }

        setFeeding(false);
        hungerCooldown = 6000;
    }

    public void tickMovement() {
        super.tickMovement();

        hungerCooldown = hungerCooldown <= 0 ? hungerCooldown : hungerCooldown-1;
        pulseCooldown = pulseCooldown <= 0 ? pulseCooldown : pulseCooldown-1;

        if (this.world.isClient && pulseCooldown <= 0) {
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
            this.pulse(true, true, 5);
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
        if(state.getMaterial().equals(Material.METAL)){
            this.pulse(false, false,8);
        }
    }

    private void pulse(boolean damaging, boolean seizure_inducing, int radius) {
        if(pulseCooldown <= 0) {
            Box pulseBox = this.getBoundingBox().expand(radius);
            List<Entity> collidedEntities = this.world.getOtherEntities(this, pulseBox, entity -> entity.isAlive() && (entity instanceof LivingEntity) && !entity.getType().equals(ElectricEel.ELECTRIC_EEL_ENTITY));
            if(damaging) {
                for(Entity entity : collidedEntities){
                    LivingEntity livingEntity = (LivingEntity) entity;

                    entity.damage(DamageSource.LIGHTNING_BOLT,charge/3);
                    if(seizure_inducing && entity instanceof WaterCreatureEntity) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(ElectricEel.SHOCK_STATUS_EFFECT,200));
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,200, 4));
                    }


                    if(!world.isClient()) {
                        ServerWorld serverWorld = (ServerWorld) this.world;
                        BlockPos startPos = new BlockPos(this.getX(),this.getY(),this.getZ());
                        BlockPos randomPos = new BlockPos(this.getX() + (livingEntity.getX()-this.getX())*this.random.nextDouble(),
                                this.getY() + (livingEntity.getY()-this.getY())*this.random.nextDouble(),this.getZ() + (livingEntity.getZ()-this.getZ())*this.random.nextDouble());
                        BlockPos endPos = new BlockPos(livingEntity.getX(),livingEntity.getY(),livingEntity.getZ());

                        double currentX = startPos.getX();
                        double currentY = startPos.getY();
                        double currentZ = startPos.getZ();

                        double t = 0.025;
                        while(t <= 1){
                            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,currentX,currentY, currentZ,2,0,0,0,0.1);
                            currentX = (1-t)*startPos.getX()+(t*randomPos.getX());
                            currentY = (1-t)*startPos.getY()+(t*randomPos.getY());
                            currentZ = (1-t)*startPos.getZ()+(t*randomPos.getZ());
                            t += 0.025;
                        }

                        t = 0.01;
                        while(t <= 1){
                            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,currentX,currentY, currentZ,2,0,0,0,0.1);      currentX = (1-t)*startPos.getX()+(t*randomPos.getX());
                            currentX = (1-t)*randomPos.getX()+(t*endPos.getX());
                            currentY = (1-t)*randomPos.getY()+(t*endPos.getY());
                            currentZ = (1-t)*randomPos.getZ()+(t*endPos.getZ());
                            t += 0.025;
                        }
                    }


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

                    if(!world.isClient()) {
                        ServerWorld serverWorld = (ServerWorld) this.world;
                        BlockPos startPos = new BlockPos(this.getX(),this.getY(),this.getZ());
                        BlockPos randomPos = new BlockPos(this.getX() + (blockPos.getX()-this.getX())*this.random.nextDouble(),
                                this.getY() + (blockPos.getY()-this.getY())*this.random.nextDouble(),this.getZ() + (blockPos.getZ()-this.getZ())*this.random.nextDouble());
                        BlockPos endPos = blockPos;

                        double currentX = startPos.getX();
                        double currentY = startPos.getY();
                        double currentZ = startPos.getZ();

                        double t = 0.010;
                        while(t <= 1){
                            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,currentX,currentY, currentZ,2,0,0,0,0.1);
                            currentX = (1-t)*startPos.getX()+(t*randomPos.getX());
                            currentY = (1-t)*startPos.getY()+(t*randomPos.getY());
                            currentZ = (1-t)*startPos.getZ()+(t*randomPos.getZ());
                            t += 0.025;
                        }

                        t = 0.01;
                        while(t <= 1){
                            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,currentX,currentY, currentZ,2,0,0,0,0.1);
                            currentX = (1-t)*randomPos.getX()+(t*endPos.getX());
                            currentY = (1-t)*randomPos.getY()+(t*endPos.getY());
                            currentZ = (1-t)*randomPos.getZ()+(t*endPos.getZ());
                            t += 0.025;
                        }
                    }

                }
            }

            pulseCooldown = 60;
        }
    }


    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        hungerCooldown = nbt.getInt("hungerCooldown");
        charge = nbt.getInt("charge");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("hungerCooldown",hungerCooldown);
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


    @Override
    public void attack(LivingEntity target, float pullProgress) {
        this.pulse(true,true, 5);
    }


    private class PulseAttackGoal extends ProjectileAttackGoal{

        private final ElectricEelEntity electricEelEntity;

        public PulseAttackGoal(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange) {
            super(mob, mobSpeed, intervalTicks, maxShootRange);
            electricEelEntity = (ElectricEelEntity) mob;
        }

        public PulseAttackGoal(RangedAttackMob mob, double mobSpeed, int minIntervalTicks, int maxIntervalTicks, float maxShootRange) {
            super(mob, mobSpeed, minIntervalTicks, maxIntervalTicks, maxShootRange);
            this.electricEelEntity = (ElectricEelEntity) mob;
        }

        public boolean canStart() {
            LivingEntity livingEntity = this.electricEelEntity.getTarget();
            if (livingEntity != null && livingEntity.isAlive() && !livingEntity.hasStatusEffect(StatusEffects.SLOWNESS)) {
                return super.canStart();
            } else {
                return false;
            }
        }
    }


}

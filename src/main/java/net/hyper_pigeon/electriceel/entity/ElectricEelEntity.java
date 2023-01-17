package net.hyper_pigeon.electriceel.entity;

import net.hyper_pigeon.electriceel.ElectricEel;
import net.hyper_pigeon.electriceel.entity.ai.goal.MoveToAndEatFishItemGoal;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.AquaticLookControl;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.goal.BreatheAirGoal;
import net.minecraft.entity.ai.goal.MoveIntoWaterGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.AmphibiousNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
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

public class ElectricEelEntity extends WaterCreatureEntity implements MultipartEntity {

//    protected static final ImmutableList<SensorType<? extends Sensor<? super ElectricEelEntity>>> SENSORS =
//            ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ITEMS, SensorType.HURT_BY);
//
//    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
//            MemoryModuleType.MOBS,
//            MemoryModuleType.VISIBLE_MOBS,
//            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
//            MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
//            MemoryModuleType.LOOK_TARGET,
//            MemoryModuleType.WALK_TARGET,
//            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
//            MemoryModuleType.PATH
//    );

    public final ElectricEelPart[] bodySegments = new ElectricEelPart[10];

    private int charge = 6;
    private int hungerCooldown = 0;
    private int pulseCooldown = 20;
    private int beamCooldown = 10;

    public boolean feeding;
    public static final TrackedData<Boolean> FEEDING = DataTracker.registerData(ElectricEelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public ElectricEelEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.navigation = new AmphibiousNavigation(this, world);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02f, 0.1f, true);
        this.lookControl = new AquaticLookControl(this,10);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 0.0F);

        for(int i = 0; i < 10; i++){
            bodySegments[i] = new ElectricEelPart(this,0.33F,0.33F, i);
            bodySegments[i].setInvisible(false);
            bodySegments[i].refreshPositionAndAngles(getX(),getY(),getZ()-i*0.15,getYaw(),getPitch());
        }

    }

    @Nullable
    public EntityData initialize(
            ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
    ) {
        this.setAir(this.getMaxAir());
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }


    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new MoveIntoWaterGoal(this));
        this.goalSelector.add(0, new BreatheAirGoal(this));
        this.goalSelector.add(1,new MoveToAndEatFishItemGoal(this));
        this.goalSelector.add(2, new SwimAroundGoal(this, 1.0, 10));
    }


    public static DefaultAttributeContainer.Builder createElectricEelAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0).add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0);
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

//    protected Brain.Profile<ElectricEelEntity> createBrainProfile() {
//        return Brain.createProfile(MEMORY_MODULES, SENSORS);
//    }
//
//    public Brain<?> getBrain() {
//        return super.getBrain();
//    }

    public int getLookPitchSpeed(){
        return 1;
    }

    public int getLookYawSpeed(){
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
            this.charge = 6;
        }
        else if(item.getStack().getItem().equals(Items.COD)){
            this.charge = 9;
        }
        else if(item.getStack().getItem().equals(Items.TROPICAL_FISH)) {
            this.charge = 12;
        }

        ItemStack itemStack = item.getStack();
        itemStack.decrement(1);
        this.heal(5.0F);
        if (itemStack.isEmpty()) {
            item.discard();
        }
        hungerCooldown = 6000;
    }

    public void tickMovement() {
        super.tickMovement();

        hungerCooldown--;

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
            this.pulse(false, false);
        }
    }

    private void pulse(boolean damaging, boolean seizure_inducing) {
        Box pulseBox = this.getBoundingBox().expand(10);
        List<Entity> collidedEntities = this.world.getOtherEntities(this, pulseBox, entity -> entity.isAlive() && (entity instanceof LivingEntity) && !entity.getType().equals(ElectricEel.ELECTRIC_EEL_ENTITY));

        for(Entity entity : collidedEntities){
            LivingEntity livingEntity = (LivingEntity) entity;
            if(damaging)
                entity.damage(DamageSource.LIGHTNING_BOLT,charge/3);
            if(seizure_inducing) {
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

}

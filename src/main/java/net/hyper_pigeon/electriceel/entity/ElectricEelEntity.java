package net.hyper_pigeon.electriceel.entity;

import net.hyper_pigeon.electriceel.ElectricEel;
import net.hyper_pigeon.electriceel.entity.ai.goal.MoveToAndEatFishEntityGoal;
import net.hyper_pigeon.electriceel.entity.ai.goal.MoveToAndEatFishItemGoal;
import net.hyper_pigeon.electriceel.entity.ai.navigation.EelNavigation;
import net.hyper_pigeon.electriceel.interfaces.EelPowered;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.entity.multipart.api.EntityPart;
import org.quiltmc.qsl.entity.multipart.api.MultipartEntity;

import java.util.ArrayList;
import java.util.List;

public class ElectricEelEntity extends WaterCreatureEntity implements MultipartEntity, RangedAttackMob, Bucketable {


    public final ElectricEelPart[] bodySegments = new ElectricEelPart[8];

    private int pulseCharge = 3;
    private int hungerCooldown = 0;
    private int pulseCooldown = 100;
    private int eatingTicks = 0;

    private ItemStack lastConsumedItemStack;

    public static final TrackedData<Boolean> FEEDING = DataTracker.registerData(ElectricEelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FROM_BUCKET = DataTracker.registerData(ElectricEelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    


    public ElectricEelEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.navigation = new EelNavigation(this, world);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.15f, 0.25f, true);
        this.lookControl = new AquaticLookControl(this,20);

        for(int i = 0; i < 8; i++){
            bodySegments[i] = new ElectricEelPart(this,0.4F,0.4F, i);
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

        this.targetSelector.add(1, new TargetGoal<>(this, FishEntity.class, false,  fishEntity -> (fishEntity instanceof FishEntity) && this.getHungerCooldown() <= 0));
    }


    public static DefaultAttributeContainer.Builder createElectricEelAttributes() {
        return MobEntity.createAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FEEDING, false);
        this.dataTracker.startTracking(FROM_BUCKET, false);
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
        return stack.getItem().equals(Items.SALMON) || stack.getItem().equals(Items.COD) || stack.getItem().equals(Items.TROPICAL_FISH) || stack.getItem().equals(Items.GLOW_INK_SAC);
    }

    protected void loot(ItemEntity item) {


        setLastConsumedItemStack(item.getStack());

        ItemStack itemStack = item.getStack();
        itemStack.decrement(1);
        this.heal(5.0F);
        if (itemStack.isEmpty()) {
            item.discard();
        }

        if(!getWorld().isClient()) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            serverWorld.playSoundFromEntity(null, this, SoundEvents.ITEM_HONEY_BOTTLE_DRINK, SoundCategory.NEUTRAL, 2.0F, 1.0F);
        }

        hungerCooldown = 1200;
        setEatingTicks(25);
    }

    public void spawnItemParticles(ItemStack stack, int count) {
        ServerWorld serverWorld = (ServerWorld)this.getWorld();
        double particleX = this.getX()+(-0.25 + (0.25 - (-0.25)) * this.getRandom().nextDouble());
        double particleY = this.getY()+(-0.1 + (0.1 - (-0.1)) * this.getRandom().nextDouble());
        double particleZ = this.getZ()+(-0.25 + (0.25 - (-0.25)) * this.getRandom().nextDouble());
        Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
        serverWorld.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM,stack),
                particleX,particleY,particleZ,count,vec3d.x,vec3d.y+0.05,vec3d.z,0.0);
    }

    public void setLastConsumedItemStack(ItemStack itemStack){
        if(itemStack.getItem().equals(Items.SALMON)){
            this.pulseCharge = 3;
            lastConsumedItemStack = new ItemStack(Items.SALMON);
        }
        else if(itemStack.getItem().equals(Items.COD)){
            this.pulseCharge = 6;
            lastConsumedItemStack = new ItemStack(Items.COD);
        }
        else if(itemStack.getItem().equals(Items.TROPICAL_FISH)){
            this.pulseCharge = 9;
            lastConsumedItemStack = new ItemStack(Items.TROPICAL_FISH);
        }
        else if(itemStack.getItem().equals(Items.GLOW_INK_SAC)) {
            this.pulseCharge = 12;
            lastConsumedItemStack = new ItemStack(Items.GLOW_INK_SAC);
        }
    }

    public void setEatingTicks(int ticks){
        this.eatingTicks = ticks;
    }

    public void baseTick(){

        hungerCooldown = hungerCooldown <= 0 ? hungerCooldown : hungerCooldown-1;
        pulseCooldown = pulseCooldown <= 0 ? pulseCooldown : pulseCooldown-1;

        if(eatingTicks > 0 && !getWorld().isClient()){
            spawnItemParticles(lastConsumedItemStack,2);
            eatingTicks--;
        }

        super.baseTick();
    }

    public void tickMovement() {
        super.tickMovement();

        for(int i = 0; i < bodySegments.length; i++){
            Entity leader = i == 0 ? this : this.bodySegments[i - 1];
            ElectricEelPart electricEelPart = this.bodySegments[i];
            electricEelPart.movePart(leader);
        }

        if(!getWorld().isClient() && this.getTarget() != null) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();

            double particleX = this.bodySegments[1].getParticleX(0.33)+(-0.75 + (0.75  - (-0.75 )) * this.getRandom().nextDouble());
            double particleY = this.bodySegments[1].getY()+(-0.75  + (0.75  - (-0.75 )) * this.getRandom().nextDouble());
            double particleZ = this.bodySegments[1].getParticleZ(0.33)+(-0.75  + (0.75  - (-0.75 )) * this.getRandom().nextDouble());

            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,particleX,particleY,particleZ,3,particleX-this.bodySegments[1].getX(),particleY-this.bodySegments[1].getY(),particleZ-this.bodySegments[1].getZ(),
                    0.1);
        }


        Box box = this.getBoundingBox().expand(0.1);
        for(BlockPos blockPos : BlockPos.iterate(
                MathHelper.floor(box.minX),
                MathHelper.floor(box.minY),
                MathHelper.floor(box.minZ),
                MathHelper.floor(box.maxX),
                MathHelper.floor(box.maxY),
                MathHelper.floor(box.maxZ)
        )) {
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            if (blockState.getBlock().equals(Blocks.LIGHTNING_ROD)) {
                this.pulse(false, false,8);
                break;
            }
        }

    }


    public boolean damage(DamageSource source, float amount) {

        if(source.getAttacker() != null){
            source.getAttacker().damage(this.getDamageSources().lightningBolt(), 3.0F);
            this.pulse(true, true, 5);
            if(getWorld().isClient()){
                getWorld().playSound(this.getX(),
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

    private void pulse(boolean damaging, boolean seizure_inducing, int radius) {
        if(pulseCooldown <= 0) {
            Box pulseBox = this.getBoundingBox().expand(radius);
            List<Entity> collidedEntities = this.getWorld().getOtherEntities(this, pulseBox, entity -> entity.isAlive() && (entity instanceof LivingEntity) && !entity.getType().equals(ElectricEel.ELECTRIC_EEL_ENTITY)
            && !entity.isSpectator());
            if(damaging) {
                for(Entity entity : collidedEntities){
                    LivingEntity livingEntity = (LivingEntity) entity;

                    entity.damage(this.getDamageSources().lightningBolt(),pulseCharge/3);
                    if(seizure_inducing && entity instanceof WaterCreatureEntity) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(ElectricEel.SHOCK_STATUS_EFFECT,200));
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,200, 4));
                    }


                    if(!getWorld().isClient()) {
                        ServerWorld serverWorld = (ServerWorld) this.getWorld();
                        Vec3d startPos = new Vec3d(this.getX(),this.getY(),this.getZ());
                        Vec3d endPos = new Vec3d(livingEntity.getX(),livingEntity.getY(),livingEntity.getZ());

                        generateBolt(startPos,endPos,5,2,serverWorld);
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
                BlockState blockState = this.getWorld().getBlockState(blockPos);
                if(blockState.isOf(Blocks.LIGHTNING_ROD)){
                    EelPowered lightningRodBlock = (EelPowered) blockState.getBlock();
                    lightningRodBlock.setEelPowered(blockState, getWorld(),blockPos,this.pulseCharge);
                    if(!getWorld().isClient()) {
                        ServerWorld serverWorld = (ServerWorld) this.getWorld();
                        Vec3d startPos = new Vec3d(this.getX(),this.getY(),this.getZ());
                        Vec3d endPos = new Vec3d(blockPos.getX(),blockPos.getY(),blockPos.getZ());
                        generateBolt(startPos,endPos,5,2,serverWorld);
                    }

                }
            }

            pulseCooldown = 100;
        }
    }


    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        hungerCooldown = nbt.getInt("hungerCooldown");
        pulseCharge = nbt.getInt("pulseCharge");
        this.setFromBucket(nbt.getBoolean("FromBucket"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("hungerCooldown",hungerCooldown);
        nbt.putInt("pulseCharge",pulseCharge);
        nbt.putBoolean("FromBucket", this.isFromBucket());
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
        if(target != null &&target.isAlive()){
            target.setAttacker(this);
            this.pulse(true,true, 5);
        }
    }

    public int getPulseCooldown(){
        return pulseCooldown;
    }

    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        return (ActionResult)Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
    }

    @Override
    public boolean isFromBucket() {
         return this.dataTracker.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        this.dataTracker.set(FROM_BUCKET, fromBucket);
    }

    @Override
    public void copyDataToStack(ItemStack stack) {
        Bucketable.copyDataToStack(this, stack);
    }

    @Override
    public void copyDataFromNbt(NbtCompound nbt) {
        Bucketable.copyDataFromNbt(this, nbt);
    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(ElectricEel.EEL_BUCKET);
    }

    @Override
    public SoundEvent getBucketedSound() {
        return SoundEvents.ENTITY_FISH_SWIM;
    }

    public void generateBolt(Vec3d startPos, Vec3d endPos, double maxOffset, int generations, ServerWorld serverWorld){
        double offsetAmount = maxOffset;

        ArrayList<Segment> segmentList = new ArrayList<>();
        segmentList.add(new Segment(startPos,endPos));

        for (int i = 0; i < generations; i++){
            ArrayList<Segment> segmentsToRemove = new ArrayList<>();
            ArrayList<Segment> segmentsToAdd = new ArrayList<>();
            for(Segment segment : segmentList){
                segmentsToRemove.add(segment);
                Vec3d midPoint = new Vec3d((segment.getStartPos().getX() + segment.getEndPos().getX())/2,(segment.getStartPos().getY() + segment.getEndPos().getY())/2,
                        (segment.getStartPos().getZ() + segment.getEndPos().getZ())/2);
                double randomValue = -offsetAmount + (offsetAmount - (-offsetAmount)) * this.getRandom().nextDouble();
                midPoint.add((segment.getStartPos().subtract(segment.getEndPos()).normalize()).crossProduct(new Vec3d(1,1,1)).multiply(randomValue));
                segmentsToAdd.add(new Segment(segment.getStartPos(),midPoint));
                segmentsToAdd.add(new Segment(midPoint,segment.getEndPos()));

//                if(this.getRandom().nextDouble() <= 0.35){
//                    Vec3d direction = midPoint.subtract(segment.getStartPos());
//                    Vec3d splitEnd = direction.rotateX(this.getRandom().nextFloat()*0.34f).
//                            rotateY(this.getRandom().nextFloat()*0.34f).rotateZ(this.getRandom().nextFloat()*0.34f).multiply(0.7).add(midPoint);
//                    segmentsToAdd.add(new Segment(midPoint, splitEnd));
//                }


            }

            segmentList.removeAll(segmentsToRemove);
            segmentList.addAll(segmentsToAdd);
            offsetAmount /= 2;

        }

        for(Segment segment : segmentList){
            double currentX  = segment.getStartPos().getX();
            double currentY  = segment.getStartPos().getY();
            double currentZ  = segment.getStartPos().getZ();

            double t = 0.05;
            while(t <= 1){
                    serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,currentX,currentY, currentZ,1,0,0,0,0.1);
                    currentX = (1-t)*segment.getStartPos().getX()+(t*segment.getEndPos().getX());
                    currentY = (1-t)*segment.getStartPos().getY()+(t*segment.getEndPos().getY());
                    currentZ = (1-t)*segment.getStartPos().getZ()+(t*segment.getEndPos().getZ());
                    t += 0.05;
            }

        }

    }


    private class Segment{
        private Vec3d startPos;
        private Vec3d endPos;

        public Segment(Vec3d startPos, Vec3d endPos){
            this.startPos = startPos;
            this.endPos = endPos;
        }

        public Vec3d getStartPos(){
            return startPos;
        }

        public Vec3d getEndPos(){
            return endPos;
        }
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

package net.hyper_pigeon.electriceel.entity.ai.goal;

import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public class MoveToAndEatFishEntityGoal extends Goal {

    private final ElectricEelEntity electricEelEntity;
    private FishEntity targetFishEntity;

    public MoveToAndEatFishEntityGoal(ElectricEelEntity electricEelEntity){
        this.electricEelEntity = electricEelEntity;
    }



    @Override
    public boolean canStart() {
        if (electricEelEntity.getHungerCooldown() <= 0) {
            List<FishEntity> list = this.electricEelEntity.getWorld().getEntitiesByClass(FishEntity.class, this.electricEelEntity.getBoundingBox().expand(10.0, 10.0, 10.0), fishEntity -> fishEntity.hasStatusEffect(StatusEffects.SLOWNESS));
            Optional<FishEntity> optional = list.stream()
                    .filter(fishEntity -> fishEntity.isInRange(electricEelEntity, 10.0))
                    .filter(fishEntity -> fishEntity.isTouchingWater())
                    .filter(electricEelEntity::canSee)
                    .findFirst();
            if(!optional.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void start(){
        List<FishEntity> list = this.electricEelEntity.getWorld().getEntitiesByClass(FishEntity.class, this.electricEelEntity.getBoundingBox().expand(10.0, 10.0, 10.0), fishEntity -> fishEntity.hasStatusEffect(StatusEffects.SLOWNESS));
        Optional<FishEntity> optional = list.stream()
                .filter(fishEntity -> fishEntity.isInRange(electricEelEntity, 10.0))
                .filter(fishEntity -> fishEntity.isTouchingWater())
                .filter(electricEelEntity::canSee)
                .findFirst();
        if(!optional.isEmpty()) {
            targetFishEntity = optional.get();
            electricEelEntity.setFeeding(true);
            electricEelEntity.getNavigation().startMovingTo(targetFishEntity,1.2F);
        }
    }

    public void stop(){
        electricEelEntity.setEatingTicks(25);
        targetFishEntity = null;
    }

    public void tick() {
        if (targetFishEntity != null) {
            electricEelEntity.getLookControl().lookAt(targetFishEntity);
            Path path;

            if(targetFishEntity.getY() > electricEelEntity.getY()){
                path =  electricEelEntity.getNavigation().
                        findPathTo
                                (new BlockPos((int)targetFishEntity.getX(),
                                        (int)targetFishEntity.getY()+2,
                                        (int)targetFishEntity.getZ()),1);
            } else {
                path =  electricEelEntity.getNavigation().
                        findPathTo
                                (new BlockPos((int)targetFishEntity.getX(),
                                        (int)targetFishEntity.getY()-2,
                                        (int)targetFishEntity.getZ()),1);
            }


            electricEelEntity.getNavigation().startMovingAlong(path,1.2F);




            if (electricEelEntity.getBoundingBox().expand(0.1).intersects(targetFishEntity.getBoundingBox())) {
                ServerWorld serverWorld = (ServerWorld) electricEelEntity.getWorld();

                serverWorld.playSoundFromEntity(null, electricEelEntity, SoundEvents.ITEM_HONEY_BOTTLE_DRINK, SoundCategory.NEUTRAL, 2.0F, 1.0F);
                electricEelEntity.setHungerCooldown(1200);
                electricEelEntity.setFeeding(false);

                ItemStack stack = getTargetItemStack();
                electricEelEntity.setLastConsumedItemStack(stack);
                targetFishEntity.remove(Entity.RemovalReason.KILLED);
            }
        }

    }

    public ItemStack getTargetItemStack(){
        if(targetFishEntity instanceof CodEntity){
            return new ItemStack(Items.COD);
        }
        else if(targetFishEntity instanceof SalmonEntity){
            return new ItemStack(Items.SALMON);
        }
        else if(targetFishEntity instanceof TropicalFishEntity){
            return new ItemStack(Items.TROPICAL_FISH);
        }
        else {
            return new ItemStack(Items.GLOW_INK_SAC);
        }
    }


}

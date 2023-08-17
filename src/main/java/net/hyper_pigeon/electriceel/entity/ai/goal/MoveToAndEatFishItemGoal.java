package net.hyper_pigeon.electriceel.entity.ai.goal;

import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public class MoveToAndEatFishItemGoal extends Goal {

    private final ElectricEelEntity electricEelEntity;
    private ItemEntity targetFishItemEntity;

    public MoveToAndEatFishItemGoal(ElectricEelEntity electricEelEntity){
        this.electricEelEntity = electricEelEntity;
    }

    @Override
    public boolean canStart() {
        if(electricEelEntity.getHungerCooldown() <= 0) {
            List<ItemEntity> list = this.electricEelEntity.getWorld().getEntitiesByClass(ItemEntity.class, this.electricEelEntity.getBoundingBox().expand(32.0, 16.0, 32.0),
                    itemEntity -> electricEelEntity.canPickupItem(itemEntity.getStack()) && itemEntity.isTouchingWater() && electricEelEntity.canSee(itemEntity));
//            Optional<ItemEntity> optional = list.stream()
//                    .filter(itemEntity -> itemEntity.isTouchingWater())
//                    .filter(electricEelEntity::canSee)
//                    .findFirst();
            if(!list.isEmpty()) {
                //this.targetFishItemEntity = optional.get();
                return true;
            }
        }
        return false;
    }


    public void start(){
        List<ItemEntity> list = this.electricEelEntity.getWorld().getEntitiesByClass(ItemEntity.class, this.electricEelEntity.getBoundingBox().expand(32.0, 16.0, 32.0),
                itemEntity -> electricEelEntity.canPickupItem(itemEntity.getStack()) && itemEntity.isTouchingWater() && electricEelEntity.canSee(itemEntity));
        Optional<ItemEntity> optional = list.stream()
                .findFirst();
        if(!optional.isEmpty()) {
            targetFishItemEntity = optional.get();
            electricEelEntity.setFeeding(true);
            electricEelEntity.getNavigation().startMovingTo(targetFishItemEntity, 1.2F);
        }
    }

//    @Override
//    public boolean shouldContinue() {
//        return targetFishItemEntity != null && !targetFishItemEntity.isRemoved() && targetFishItemEntity.isTouchingWater()
//                && electricEelEntity.canSee(targetFishItemEntity);
//    }


    public void stop() {
        electricEelEntity.setFeeding(false);
        targetFishItemEntity = null;
    }

    public void tick() {
        if (targetFishItemEntity != null) {
            electricEelEntity.getLookControl().lookAt(targetFishItemEntity);

            Path path;
            if(targetFishItemEntity.getY() > electricEelEntity.getY()){
                path =  electricEelEntity.getNavigation().
                        findPathTo
                                (new BlockPos((int)targetFishItemEntity.getX(),
                                        (int)targetFishItemEntity.getY()+2,
                                        (int)targetFishItemEntity.getZ()),1);
            } else {
                path =  electricEelEntity.getNavigation().
                        findPathTo
                                (new BlockPos((int)targetFishItemEntity.getX(),
                                        (int)targetFishItemEntity.getY()-2,
                                        (int)targetFishItemEntity.getZ()),1);
            }


            electricEelEntity.getNavigation().startMovingAlong(path,1.2F);

        }
    }


}

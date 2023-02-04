package net.hyper_pigeon.electriceel.entity.ai.goal;

import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

public class MoveToAndEatFishItemGoal extends Goal {

    private final ElectricEelEntity electricEelEntity;
    private ItemEntity targetFishItemStack;

    public MoveToAndEatFishItemGoal(ElectricEelEntity electricEelEntity){
        this.electricEelEntity = electricEelEntity;
    }

    @Override
    public boolean canStart() {
        if(electricEelEntity.getHungerCooldown() <= 0) {
            List<ItemEntity> list = this.electricEelEntity.world.getEntitiesByClass(ItemEntity.class, this.electricEelEntity.getBoundingBox().expand(32.0, 16.0, 32.0), itemEntity -> itemEntity.getStack().getItem().equals(Items.COD) || itemEntity.getStack().getItem().equals(Items.SALMON) || itemEntity.getStack().getItem().equals(Items.TROPICAL_FISH));
            Optional<ItemEntity> optional = list.stream()
                    .filter(itemEntity -> electricEelEntity.canGather(itemEntity.getStack()))
                    .filter(itemEntity -> itemEntity.isInRange(electricEelEntity, 32.0))
                    .filter(itemEntity -> itemEntity.isTouchingWater())
                    .filter(electricEelEntity::canSee)
                    .findFirst();
            if(!optional.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void start(){
        List<ItemEntity> list = this.electricEelEntity.world.getEntitiesByClass(ItemEntity.class, this.electricEelEntity.getBoundingBox().expand(32.0, 16.0, 32.0), itemEntity -> itemEntity.getStack().getItem().equals(Items.COD) || itemEntity.getStack().getItem().equals(Items.SALMON) || itemEntity.getStack().getItem().equals(Items.TROPICAL_FISH));
        Optional<ItemEntity> optional = list.stream()
                .filter(itemEntity -> electricEelEntity.canGather(itemEntity.getStack()))
                .filter(itemEntity -> itemEntity.isInRange(electricEelEntity, 32.0))
                .filter(itemEntity -> itemEntity.isTouchingWater())
                .filter(electricEelEntity::canSee)
                .findFirst();

        this.targetFishItemStack = optional.get();
        electricEelEntity.setFeeding(true);
        electricEelEntity.getNavigation().startMovingTo(targetFishItemStack,1.2F);
    }

    public void stop() {
        electricEelEntity.setFeeding(false);
        targetFishItemStack = null;
    }

    public void tick() {
        if (targetFishItemStack != null) {
            electricEelEntity.getLookControl().lookAt(targetFishItemStack);
            Path path;

            if(targetFishItemStack.getY() > electricEelEntity.getY()){
                path =  electricEelEntity.getNavigation().
                        findPathTo
                                (new BlockPos(targetFishItemStack.getX(),
                                        targetFishItemStack.getY()+2,
                                        targetFishItemStack.getZ()),1);
            } else {
                path =  electricEelEntity.getNavigation().
                        findPathTo
                                (new BlockPos(targetFishItemStack.getX(),
                                        targetFishItemStack.getY()-2,
                                        targetFishItemStack.getZ()),1);
            }


            electricEelEntity.getNavigation().startMovingAlong(path,1.2F);

        }
    }

}
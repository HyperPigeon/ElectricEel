package net.hyper_pigeon.electriceel.entity.ai.goal;

import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EelSwimGoal extends SwimAroundGoal {
    private final ElectricEelEntity electricEelEntity;
    private ItemEntity targetFishItemStack;

    public EelSwimGoal(ElectricEelEntity electricEelEntity, double d, int i) {
        super(electricEelEntity, d, i);
        this.electricEelEntity = electricEelEntity;
    }

        @Nullable
    protected Vec3d getWanderTarget() {
        if(electricEelEntity.getHungerCooldown() <= 0) {
            List<ItemEntity> list = this.electricEelEntity.world.getEntitiesByClass(ItemEntity.class, this.electricEelEntity.getBoundingBox().expand(32.0, 16.0, 32.0), itemEntity -> itemEntity.getStack().getItem().equals(Items.COD) || itemEntity.getStack().getItem().equals(Items.SALMON) || itemEntity.getStack().getItem().equals(Items.TROPICAL_FISH));
            Optional<ItemEntity> optional = list.stream()
                    .filter(itemEntity -> electricEelEntity.canGather(itemEntity.getStack()))
                    .filter(itemEntity -> itemEntity.isInRange(electricEelEntity, 32.0))
                    .filter(itemEntity -> itemEntity.isTouchingWater())
                    .filter(electricEelEntity::canSee)
                    .findFirst();
            if(!optional.isEmpty()) {
                targetFishItemStack = optional.get();
            }
            electricEelEntity.feeding = true;
        }

        return this.targetFishItemStack != null && !this.targetFishItemStack.isRemoved() ? new Vec3d(targetFishItemStack.getX(),targetFishItemStack.getY(),targetZ): LookTargetUtil.find(this.mob, 10, 7);
    }
}

package net.hyper_pigeon.electriceel.entity.ai.control;

import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.MobEntity;

public class EelLookControl extends LookControl {

    public EelLookControl(MobEntity entity) {
        super(entity);
        this.yawSpeed = 1F;
    }

    public void tick() {
        if (this.lookAtCooldown > 0) {
            --this.lookAtCooldown;
            this.getTargetYaw().ifPresent(float_ -> this.entity.headYaw = this.changeAngle(this.entity.headYaw, float_ + 20.0F, this.yawSpeed));
        } else {
            if (this.entity.getNavigation().isIdle()) {
                this.entity.setPitch(this.changeAngle(this.entity.getPitch(), 0.0F, 5.0F));
            }
        }

    }
}

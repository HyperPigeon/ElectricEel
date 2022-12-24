package net.hyper_pigeon.electriceel.entity.ai.control;

import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.mob.MobEntity;

public class EelMoveControl extends AquaticMoveControl {
    public EelMoveControl(MobEntity entity, int pitchChange, int yawChange, float speedInWater, float speedInAir, boolean buoyant) {
        super(entity, pitchChange, yawChange, speedInWater, speedInAir, buoyant);
    }
}

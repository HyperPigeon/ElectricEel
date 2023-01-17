package net.hyper_pigeon.electriceel.entity.ai.navigation;

import net.minecraft.entity.ai.pathing.AmphibiousNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EelNavigation extends AmphibiousNavigation {
    public EelNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    protected Vec3d getPos() {
        return new Vec3d(this.entity.getX(), this.entity.getY(), this.entity.getZ());
    }
}

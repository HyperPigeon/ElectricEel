package net.hyper_pigeon.electriceel.entity.ai.navigation;

import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.minecraft.entity.ai.pathing.AmphibiousNavigation;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class EelNavigation extends AmphibiousNavigation {
    public EelNavigation(ElectricEelEntity electricEelEntity, World world) {
        super(electricEelEntity, world);
    }

    @Override
    public void tick() {
        ++this.tickCount;
        if (this.shouldRecalculate) {
            this.recalculatePath();
        }

        if (!this.isIdle()) {
            if (this.isAtValidPosition()) {
                this.continueFollowingPath();
            } else if (this.currentPath != null && !this.currentPath.isFinished()) {
                Vec3d vec3d = this.getPos();
                Vec3d vec3d2 = this.currentPath.getNodePosition(this.entity);
                if (vec3d.y > vec3d2.y
                        && !this.entity.isOnGround()
                        && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d2.x)
                        && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d2.z)) {
                    this.currentPath.next();
                }
            }

            DebugInfoSender.sendPathfindingData(this.world, this.entity, this.currentPath, this.nodeReachProximity);
            if (!this.isIdle()) {
                Vec3d vec3d = this.currentPath.getNodePosition(this.entity);
                this.entity.getMoveControl().moveTo(vec3d.x, this.adjustTargetY(vec3d), vec3d.z, this.speed);
            }
        }
    }

    protected void continueFollowingPath() {
        Vec3d vec3d = this.getPos();
        this.nodeReachProximity = this.entity.getWidth() > 0.75F ? this.entity.getWidth() / 2.0F : 0.75F - this.entity.getWidth() / 2.0F;
        Vec3i vec3i = this.currentPath.getCurrentNodePos();
        double d = Math.abs(this.entity.getX() - ((double)vec3i.getX() + 0.5));
        double e = Math.abs(this.entity.getY() - (double)vec3i.getY());
        double f = Math.abs(this.entity.getZ() - ((double)vec3i.getZ() + 0.5));

        boolean bl = d < (double)this.nodeReachProximity && f < (double)this.nodeReachProximity && e <= 0.01;
        if (bl) {
            this.currentPath.next();
        }

        this.checkTimeouts(vec3d);
    }

    private boolean shouldJumpToNextNode(Vec3d currentPos) {
        if (this.currentPath.getCurrentNodeIndex() + 1 >= this.currentPath.getLength()) {
            return false;
        } else {
            Vec3d vec3d = Vec3d.ofBottomCenter(this.currentPath.getCurrentNodePos());
            if (!currentPos.isInRange(vec3d, 0.1)) {
                return false;
            } else if (this.canPathDirectlyThrough(currentPos, this.currentPath.getNodePosition(this.entity))) {
                return true;
            } else {
                Vec3d vec3d2 = Vec3d.ofBottomCenter(this.currentPath.getNodePos(this.currentPath.getCurrentNodeIndex() + 1));
                Vec3d vec3d3 = vec3d.subtract(currentPos);
                Vec3d vec3d4 = vec3d2.subtract(currentPos);
                double d = vec3d3.lengthSquared();
                double e = vec3d4.lengthSquared();
                boolean bl = e < d;
                boolean bl2 = d < 0.5;
                if (!bl && !bl2) {
                    return false;
                } else {
                    Vec3d vec3d5 = vec3d3.normalize();
                    Vec3d vec3d6 = vec3d4.normalize();
                    return vec3d6.dotProduct(vec3d5) < 0.0;
                }
            }
        }
    }
}

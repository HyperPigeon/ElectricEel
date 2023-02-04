package net.hyper_pigeon.electriceel.entity;

import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.qsl.entity.multipart.api.AbstractEntityPart;

public class ElectricEelPart extends AbstractEntityPart<ElectricEelEntity> {

    private final int partNumber;

    public ElectricEelPart(ElectricEelEntity owner, float width, float height, int partNumber) {
        super(owner, width, height);
        this.partNumber = partNumber;
    }

    public void tick(){
        super.tick();
        if (!this.world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;
            serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,this.getParticleX(0.5),this.getRandomBodyY() - 0.25,
                    this.getParticleZ(0.5), 2,0,0,0,0);
        }
    }

    public void movePart(Entity leader){
        double followX = leader.getX();
        double followY = leader.getY();
        double followZ = leader.getZ();

        float yaw = (float) (((leader.getYaw() + 180) * Math.PI) / 180.0F);
        float pitch = (float) (((leader.getPitch() + 180) * Math.PI) / 180.0F);

        double targetX = -Math.sin(yaw);
        double targetZ = Math.cos(yaw);
        double targetY = -Math.sin(pitch);


        Vec3d diff = new Vec3d(this.getX() - followX, this.getY() - followY, this.getZ() - followZ);
        diff = diff.normalize();

        diff = diff.add(targetX, targetY, targetZ).normalize();

        double f = 0.3D;

        double destX = followX + f * diff.getX();
        double destY = followY + f * diff.getY();
        double destZ = followZ + f * diff.getZ();

        this.refreshPositionAndAngles(destX,destY,destZ,(float)(Math.atan2(diff.getZ(), diff.getX()) * 180.0F / Math.PI) + 90.0F,  MathHelper.lerpAngleDegrees(0.10f,this.getPitch(),leader.getPitch()));
        //this.refreshPositionAndAngles(destX,destY,destZ,(float)(Math.atan2(diff.getZ(), diff.getX()) * 180.0F / Math.PI) + 90.0F,  MathHelper.lerpAngleDegrees(0.10f,this.getPitch(),-(float) (Math.atan2(diff.getY(), diff.lengthSquared()) * 180.0D / Math.PI)));

    }




}

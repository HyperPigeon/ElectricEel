package net.hyper_pigeon.electriceel.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import org.quiltmc.qsl.entity.multipart.api.AbstractEntityPart;

public class ElectricEelPart extends AbstractEntityPart {

    public ElectricEelPart(Entity owner, float width, float height) {
        super(owner, width, height);
    }
}

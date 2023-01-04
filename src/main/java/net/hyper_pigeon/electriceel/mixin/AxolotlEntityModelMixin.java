package net.hyper_pigeon.electriceel.mixin;

import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.entity.passive.AxolotlEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AxolotlEntityModel.class)
public abstract class AxolotlEntityModelMixin {

    @Inject(method = "setAngles(Lnet/minecraft/entity/passive/AxolotlEntity;FFFFF)V", at = @At("HEAD"))
    public void printHeadYaw(AxolotlEntity axolotlEntity, float f, float g, float h, float i, float j, CallbackInfo ci){
        if(i != 0.0){
            System.out.println(i);
        }
        else {
            System.out.println("zero");
        }
    }

}

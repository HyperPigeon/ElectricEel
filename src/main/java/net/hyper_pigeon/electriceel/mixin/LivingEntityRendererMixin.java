package net.hyper_pigeon.electriceel.mixin;

import net.hyper_pigeon.electriceel.ElectricEel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ClientOnly
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(method = "isShaking",at = @At("RETURN"), cancellable = true)
    public void isShocked(LivingEntity entity, CallbackInfoReturnable<Boolean> cir){
        if(entity.hasStatusEffect(ElectricEel.SHOCK_STATUS_EFFECT)){
            cir.setReturnValue(true);
        }
    }

}

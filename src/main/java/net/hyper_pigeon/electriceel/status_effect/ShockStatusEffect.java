package net.hyper_pigeon.electriceel.status_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;

public class ShockStatusEffect extends StatusEffect {
    public ShockStatusEffect(StatusEffectType type, int color) {
        super(type, color);
    }
}

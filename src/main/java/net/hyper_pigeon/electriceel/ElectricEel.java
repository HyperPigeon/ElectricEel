package net.hyper_pigeon.electriceel;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.hyper_pigeon.electriceel.status_effect.ShockStatusEffect;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

import java.util.function.Supplier;

public class ElectricEel implements ModInitializer {

    public static final EntityType<ElectricEelEntity> ELECTRIC_EEL_ENTITY = Registry.register(
            Registries.ENTITY_TYPE, new Identifier("electric_eel", "electric_eel"),
            QuiltEntityTypeBuilder.create(SpawnGroup.WATER_CREATURE, ElectricEelEntity::new).setDimensions(EntityDimensions.changing(0.5F, 0.5F)).build()
    );

    public static final ShockStatusEffect SHOCK_STATUS_EFFECT = Registry.register(Registries.STATUS_EFFECT, new Identifier("electric_eel","shock"),new ShockStatusEffect(StatusEffectType.HARMFUL,11141120));

    @Override
    public void onInitialize(ModContainer mod) {
        FabricDefaultAttributeRegistry.register(ELECTRIC_EEL_ENTITY, ElectricEelEntity.createElectricEelAttributes().build());
    }

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
        return (SensorType<U>)Registry.register(Registries.SENSOR_TYPE, new Identifier(id), new SensorType(factory));
    }
}

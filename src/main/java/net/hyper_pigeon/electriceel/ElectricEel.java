package net.hyper_pigeon.electriceel;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

public class ElectricEel implements ModInitializer {

    public static final EntityType<ElectricEelEntity> ELECTRIC_EEL_ENTITY = Registry.register(
            Registries.ENTITY_TYPE, new Identifier("electric_eel", "electric_eel"),
            QuiltEntityTypeBuilder.create(SpawnGroup.WATER_CREATURE, ElectricEelEntity::new).setDimensions(EntityDimensions.changing(1F, 0.75F)).build()
    );


    @Override
    public void onInitialize(ModContainer mod) {
        FabricDefaultAttributeRegistry.register(ELECTRIC_EEL_ENTITY, ElectricEelEntity.createElectricEelAttributes().build());
    }
}

package net.hyper_pigeon.electriceel;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.hyper_pigeon.electriceel.status_effect.ShockStatusEffect;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;
import org.quiltmc.qsl.worldgen.biome.api.BiomeSelectors;
import virtuoel.statement.api.StateRefresher;

import java.util.function.Supplier;

public class ElectricEel implements ModInitializer {

    public static final EntityType<ElectricEelEntity> ELECTRIC_EEL_ENTITY = Registry.register(
            Registries.ENTITY_TYPE, new Identifier("electric_eel", "electric_eel"),
            QuiltEntityTypeBuilder.create(SpawnGroup.WATER_CREATURE, ElectricEelEntity::new).setDimensions(EntityDimensions.changing(0.5F, 0.5F)).allowSpawningInside(Blocks.WATER).build()
    );

    public static final ShockStatusEffect SHOCK_STATUS_EFFECT = Registry.register(Registries.STATUS_EFFECT, new Identifier("electric_eel","shock"),new ShockStatusEffect(StatusEffectType.HARMFUL,11141120));

    public static final Item EEL_BUCKET = Registry.register(Registries.ITEM, new Identifier("electric_eel","eel_bucket"), new EntityBucketItem(ELECTRIC_EEL_ENTITY, Fluids.WATER,
            SoundEvents.ITEM_BUCKET_EMPTY_FISH, new Item.Settings().maxCount(1)));

    public static final IntProperty EEL_POWER = Properties.POWER;


    public static final TagKey<Biome> ELECTRIC_EEL_SPAWN_BIOMES = TagKey.of(RegistryKeys.BIOME, new Identifier("electric_eel", "electric_eel_spawn_biomes"));

    @Override
    public void onInitialize(ModContainer mod) {
        FabricDefaultAttributeRegistry.register(ELECTRIC_EEL_ENTITY, ElectricEelEntity.createElectricEelAttributes().build());
        StateRefresher.INSTANCE.addBlockProperty(Blocks.LIGHTNING_ROD,EEL_POWER,0);

        SpawnRestriction.register(ELECTRIC_EEL_ENTITY, SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::m_rlvqolvj);
        BiomeModifications.addSpawn(BiomeSelectors.isIn(ELECTRIC_EEL_SPAWN_BIOMES),SpawnGroup.CREATURE, ELECTRIC_EEL_ENTITY,
                1, 1,1);

    }

}

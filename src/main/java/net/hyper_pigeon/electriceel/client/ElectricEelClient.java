package net.hyper_pigeon.electriceel.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.hyper_pigeon.electriceel.ElectricEel;
import net.hyper_pigeon.electriceel.client.render.entity.ElectricEelEntityRenderer;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

@Environment(EnvType.CLIENT)
public class ElectricEelClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        EntityRendererRegistry.register(ElectricEel.ELECTRIC_EEL_ENTITY, ElectricEelEntityRenderer::new);
    }
}

package net.hyper_pigeon.electriceel.client.render.entity;

import net.hyper_pigeon.electriceel.client.render.entity.model.ElectricEelEntityModel;
import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class ElectricEelEntityRenderer extends MobEntityRenderer<ElectricEelEntity, ElectricEelEntityModel<ElectricEelEntity>> {

    public static final Identifier TEXTURE = new Identifier("electric_eel","textures/entity/mob/electric_eel.png");

    public ElectricEelEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ElectricEelEntityModel<>(ElectricEelEntityModel.getTexturedModelData().createModel()), 0.7f);
    }

    @Override
    public Identifier getTexture(ElectricEelEntity entity) {
        return TEXTURE;
    }


}

package net.hyper_pigeon.electriceel.client.render.entity;

import net.hyper_pigeon.electriceel.client.render.entity.model.ElectricEelEntityModel;
import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
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

    @Override
    public void render(ElectricEelEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);

//        if(!mobEntity.isInvisible()){
//
//
//            for(int k = 0; i < mobEntity.bodySegments.length; k++){
//
//                matrixStack.push();
//
//                float yawDiff = mobEntity.getYaw() - mobEntity.prevYaw;
//                if (yawDiff > 180) {
//                    yawDiff -= 360;
//                } else if (yawDiff < -180) {
//                    yawDiff += 360;
//                }
//                float yaw2 = mobEntity.prevYaw + yawDiff * g;
//
//
//                matrixStack.multiply(Axis.Y_POSITIVE.rotationDegrees(yaw2));
//                matrixStack.multiply(Axis.X_POSITIVE.rotationDegrees(mobEntity.getPitch()));
//
//                matrixStack.pop();
//            }
//        }

    }
}
